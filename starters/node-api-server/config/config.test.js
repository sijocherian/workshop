const config = require('./config.global');

config.env = 'test';
config.hostname = 'test.example';

config.log.name = 'console';
config.log.level = 'debug';

module.exports = config;
