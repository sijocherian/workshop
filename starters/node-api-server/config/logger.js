const log4js = require('log4js');
const cfg = require('./index.js');

log4js.configure({
    appenders: {
        out: { type: 'console' },
    },
    categories: {
        default: { appenders: ['out'], level: cfg.log.level },
    },
});

function getLogger(module) {
    const logger = log4js.getLogger(module);
    logger.level = cfg.log.level;
    return logger;
}

module.exports = {
    getLogger,
};
