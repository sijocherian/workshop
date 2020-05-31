/*jshint node: true*/
/*jshint esversion: 6 */
'use strict';

const fs = require('fs');
const express = require('express');
const application = express();
const basicRoutes = require('./routes/route-api');

/*application.get('/version', function(req, result) {
    result.send({ version: '0.0.1' });
});*/
application.use('/', basicRoutes);

application.use((req, res, next) => {
    const error = new Error('No route found');
    error.status = 404;
    next(error);
});

application.use((error, req, res, next) => {
    res.status(error.status || 500);
    res.json({
        error: {
            message: error.message,
        },
    });
});

const port = process.env.PORT || process.env.VCAP_APP_PORT || 3000;
application.listen(port, function() {
    console.log('Server running on port: %d', port);
});

//for unit test
module.exports = application;
