const { exec } = require('child_process');
const fs = require('fs');

console.log('Running API tests...');

exec('newman run tests.json -n 5 --reporters json --reporter-json-export results.json', (error, stdout, stderr) => {
    if (error) {
        console.log('Error:', error);
        return;
    }

    console.log('Tests completed! Creating table...');
    createTable();
});

function createTable() {
    const data = JSON.parse(fs.readFileSync('results.json', 'utf8'));

    let results = {};
    data.run.executions.forEach(test => {
        let name = test.item.name;
        if (!results[name]) results[name] = [];
        results[name].push(test.response.responseTime);
    });

    let csv = 'Endpoint,Avg Time (ms),Min Time (ms),Max Time (ms),Tests\n';
    for (let endpoint in results) {
        let times = results[endpoint];
        let avg = (times.reduce((a,b) => a + b, 0) / times.length).toFixed(2);
        let min = Math.min(...times);
        let max = Math.max(...times);

        csv += `${endpoint},${avg},${min},${max},${times.length}\n`;
    }

    fs.writeFileSync('performance-table.csv', csv);
    console.log('Table saved: performance-table.csv');
    console.log('Results:');
    console.log(csv);
}