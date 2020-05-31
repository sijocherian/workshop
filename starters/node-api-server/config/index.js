const env = process.env.NODE_ENV || 'test';

// eslint-disable-next-line
const cfg = require('./config.' + env);

module.exports = cfg;
