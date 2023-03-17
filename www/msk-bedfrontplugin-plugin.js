var exec = require('cordova/exec');

module.exports = {
    
    initAsync: function (success, error) {
        exec(success, error, 'MSKBedfrontPlugin', 'initAsync');
    },
    connect: function (success, error) {
        exec(success, error, 'MSKBedfrontPlugin', 'connect');
    },
    disconnect: function (success, error) {
        exec(success, error, 'MSKBedfrontPlugin', 'disconnect');
    },
    getSerialNumber: function (success, error) {
        exec(success, error, 'MSKBedfrontPlugin', 'getSerialNumber');
    }
}