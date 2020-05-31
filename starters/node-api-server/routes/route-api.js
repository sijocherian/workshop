const express = require('express');

const simpleController = require('../controllers/simple');

const router = express.Router();

// version for liveness check
router.get(
    '/version',
    (req, res, next) => {
        return next();
    },
    simpleController.version
);

module.exports = router;
