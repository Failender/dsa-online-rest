var logs = [];
var execute = function(params) {
    var result = fun(params);
    return {result: result, logs: logs}
}
var log = function(param, level) {
try {
    param = param.toString();
}
catch (e){
    param = JSON.stringify(param);
}
print(param)
if(level) {
    logs.push({value:param, level: level})
}
else {
    logs.push({value:param, level:'info'})
}

}



var fun = function(params) {