const url = 'mongodb://127.0.0.1:27017';
const MongoClient = require('mongodb').MongoClient;

exports.connection = MongoClient.connect(url, {
    useNewUrlParser: true,
    useUnifiedTopology: true
})
