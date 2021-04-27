const bcrypt = require('bcrypt')
const {v4 : uuidv4} = require('uuid')
var connection = require('./mongoConnection').connection
const dbname = 'starvin'

const verbose = true;
function vprint(s){
  if(verbose){
    console.log(s);
  }
}

/*
User object:
{
  "name": "<name>",
  "email":  "<email>",
  "password": "<password>",
  "userid":  "uniqid generated",
  "art": ["artid","artid","artid"],
  "token": "uniqid generated"
}
*/

function createUser(name, email, password){
  return new Promise((resolve, reject) => {
    var users = null
    var user = null
    var userObj = null
    var hash = null
    vprint('Attempting to create user: '+email)
    connection.then(client => {
      vprint('Connected to mongodb')
      return client.db(dbname).collection('users')
    }).then(res => {
      users = res
      //Check if email exists:
      return users.findOne({'email': email})
    }).then(res => {
      user = res
      if(user == null) { //Email not taken
          vprint('User doesnt exist')
          userObj = {}
          userObj['name'] = name
          userObj['email'] = email
          userObj['password'] = password
          userObj['token'] = ''
          userObj['userid'] = uuidv4()
          userObj['art'] = []
          let hashstr = email + password
          return bcrypt.hash(hashstr, 10)
      } else {
        reject({'error': 'Email was taken'})
      }
    }).then(res => {
      hash = res
      userObj['password'] = hash
      vprint('Hashed password to '+hash)
      return users.insertOne(userObj)
    }).then(res => {
      vprint('Inserted '+res.insertedCount);
      resolve('User created.')
    }).catch(err => {
      vprint(err)
      reject(err)
    })
  });
}

function login(email, password){
  return new Promise(function(resolve, reject) {
    vprint('Attempting to login: '+email)
    var users = null
    var user = null
    connection.then(client => {
      vprint('Connected to mongodb')
      return client.db(dbname).collection('users')
    }).then(res => {
      users = res
      return users.findOne({'email': email})
    }).then(res => {
      user = res
      if(user != null){
        let hashstr = email + password
        return bcrypt.compare(hashstr,user.password)
      } else {
        vprint('User not found')
        reject({'error': 'User not found.'})
      }
    }).then(res => {
      if(res){
        let token = uuidv4()
        user.token = token
        return users.findOneAndUpdate({'email': email}, {'$set': {'token': token}})
      } else {
        vprint('Incorrect email or password')
        reject({'Error': 'Incorrect email or password'})
      }
    }).then(res => {
      vprint('Successfully signed in')
      resolve(user)
    }).catch(err => {
      vprint(err)
      reject(err)
    })
  });
}

exports.createUser = createUser
exports.login = login
