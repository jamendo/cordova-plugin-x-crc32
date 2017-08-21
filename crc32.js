var exec = cordova.require('cordova/exec');

exports.crc32 = function(filePath, callback) {
    var win = function(result) {
        callback(result);
    };
    var fail = function(error) {
        console.error(error);
    };
    exec(win, fail, 'Crc32', 'crc32', [filePath]);
};
