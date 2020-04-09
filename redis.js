const redis = require("redis");
const client = redis.createClient();

client.on("error", function(){
    console.error(error);
});

client.on('connect', function () {
    console.log('connected');
});

for (let index = 1; index < 100; index++) {
    
    client.sadd("numerosParaGeracaoDeCartelas", index);
    
}

for (let indexUser = 1; indexUser < 3; indexUser++) {
        
    client.srandmember("numerosParaGeracaoDeCartelas",15,function(err,response){
        if (response != false) {
            client.sadd("cartela".concat(indexUser), response);
            console.log(response);
        }
    });

    client.hset("user".concat(indexUser), "name", "user".concat(indexUser));
    client.hset("user".concat(indexUser), "bcartela", "cartela".concat(indexUser));
    client.hset("user".concat(indexUser), "bscore", "score".concat(indexUser));

}

client.smembers("numerosParaGeracaoDeCartelas", redis.print);

