//const url = 'mongodb://127.0.0.1:27017';
const url = 'mongodb+srv://starvinapp:<password>@cluster0.ymx7o.mongodb.net/myFirstDatabase?retryWrites=true&w=majority'
const MongoClient = require('mongodb').MongoClient;

exports.connection = MongoClient.connect(url, {
    useNewUrlParser: true,
    useUnifiedTopology: true
})
