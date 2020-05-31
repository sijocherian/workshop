/* eslint-disable complexity */
const pjson = require('../package.json');

// eslint-disable-next-line no-unused-vars
exports.version = (req, res, next) => {
    res.status(200).json({
        version: pjson.version,
    });
};
