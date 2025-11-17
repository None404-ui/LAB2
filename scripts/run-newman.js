const newman = require('newman');
const fs = require('fs/promises');
const path = require('path');

const ROOT = process.cwd();
const RESULTS_DIR = path.join(ROOT, 'newman_results');
const COLLECTION = path.join(ROOT, 'postman', 'Lab2_API.postman_collection.json');
const ENVIRONMENT = path.join(ROOT, 'postman', 'Lab2_API.postman_environment.json');
const SUMMARY_JSON = path.join(RESULTS_DIR, 'summary.json');
const PERFORMANCE_MD = path.join(ROOT, 'performance_results.md');
const ITERATIONS = Number(process.env.API_BENCH_ITERATIONS) || 5;

const requestMetrics = new Map();

async function ensureDirs() {
  await fs.mkdir(RESULTS_DIR, { recursive: true });
}

function recordExecutions(executions) {
  executions.forEach((exec) => {
    const method = exec.request?.method || 'UNKNOWN';
    const url = exec.request?.url?.toString() || exec.request?.url?.raw || 'UNKNOWN';
    const key = `${method} ${url}`;

    if (!requestMetrics.has(key)) {
      requestMetrics.set(key, {
        method,
        url,
        label: exec.item?.name || key,
        times: [],
        attempts: 0,
        successes: 0,
      });
    }

    const bucket = requestMetrics.get(key);
    bucket.attempts += 1;

    if (exec.response && typeof exec.response.responseTime === 'number') {
      bucket.times.push(exec.response.responseTime);
    }

    const status = exec.response?.code;
    const hasAssertionError = Array.isArray(exec.assertions)
      ? exec.assertions.some((a) => Boolean(a.error))
      : false;

    if (status >= 200 && status < 300 && !hasAssertionError) {
      bucket.successes += 1;
    }
  });
}

function percentile(sorted, p) {
  if (!sorted.length) return null;
  const idx = Math.ceil((p / 100) * sorted.length) - 1;
  return sorted[Math.max(0, Math.min(idx, sorted.length - 1))];
}

function formatNumber(value) {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return '—';
  }
  return Number(value).toFixed(2);
}

function buildStats(times) {
  if (!times.length) {
    return {
      average: null,
      median: null,
      p95: null,
      min: null,
      max: null,
      samples: 0,
    };
  }

  const sorted = [...times].sort((a, b) => a - b);
  const sum = sorted.reduce((acc, val) => acc + val, 0);
  const average = sum / sorted.length;
  const half = Math.floor(sorted.length / 2);
  const median =
    sorted.length % 2 === 0 ? (sorted[half - 1] + sorted[half]) / 2 : sorted[half];

  return {
    average,
    median,
    p95: percentile(sorted, 95),
    min: sorted[0],
    max: sorted[sorted.length - 1],
    samples: sorted.length,
  };
}

function buildMarkdown(statsPayload) {
  const lines = [];
  lines.push('# Результаты тестирования производительности API');
  lines.push('');
  lines.push(`Сгенерировано: ${new Date(statsPayload.generatedAt).toLocaleString('ru-RU')}`);
  lines.push(`Количество итераций: ${statsPayload.iterations}`);
  lines.push('');
  lines.push('| Эндпоинт | Среднее (мс) | Медиана (мс) | P95 (мс) | Мин (мс) | Макс (мс) | Успешность | Выборок |');
  lines.push('|----------|--------------|--------------|----------|----------|----------|------------|---------|');

  statsPayload.requests.forEach((req) => {
    lines.push(
      `| \`${req.method} ${req.url}\` | ${formatNumber(req.stats.average)} | ${formatNumber(
        req.stats.median
      )} | ${formatNumber(req.stats.p95)} | ${formatNumber(req.stats.min)} | ${formatNumber(
        req.stats.max
      )} | ${(req.successRate * 100).toFixed(2)}% | ${req.stats.samples} |`
    );
  });

  lines.push('');
  lines.push('> Таблица формируется автоматически скриптом `scripts/run-newman.js`.');
  return lines.join('\n');
}

async function saveArtifacts(payload) {
  await fs.writeFile(SUMMARY_JSON, JSON.stringify(payload, null, 2), 'utf8');
  await fs.writeFile(PERFORMANCE_MD, buildMarkdown(payload), 'utf8');
}

async function runIteration(iteration) {
  return new Promise((resolve, reject) => {
    newman.run(
      {
        collection: COLLECTION,
        environment: ENVIRONMENT,
        reporters: ['json'],
        reporter: {
          json: {
            export: path.join(RESULTS_DIR, `run_${iteration}.json`),
          },
        },
      },
      (error, summary) => {
        if (error) {
          return reject(error);
        }
        recordExecutions(summary.run.executions || []);
        return resolve({
          totalDuration: summary.run?.timings?.completed - summary.run?.timings?.started,
        });
      }
    );
  });
}

async function execute() {
  await ensureDirs();

  for (let i = 1; i <= ITERATIONS; i += 1) {
    /* eslint-disable no-console */
    console.log(`Итерация ${i}/${ITERATIONS}`);
    try {
      const { totalDuration } = await runIteration(i);
      console.log(`  ✓ завершено за ${totalDuration ?? 'n/a'} мс`);
    } catch (error) {
      console.error(`  ✗ ошибка: ${error.message}`);
    }
    /* eslint-enable no-console */
  }

  const requests = Array.from(requestMetrics.values()).map((entry) => {
    const stats = buildStats(entry.times);
    return {
      label: entry.label,
      method: entry.method,
      url: entry.url,
      stats,
      successRate: entry.attempts === 0 ? 0 : entry.successes / entry.attempts,
    };
  });

  const payload = {
    generatedAt: new Date().toISOString(),
    iterations: ITERATIONS,
    requests,
  };

  await saveArtifacts(payload);
  return payload;
}

if (require.main === module) {
  execute()
    .then(() => {
      console.log('✔️  Аналитика обновлена.');
    })
    .catch((error) => {
      console.error('Не удалось завершить тестирование:', error);
      process.exitCode = 1;
    });
}

module.exports = { execute };

