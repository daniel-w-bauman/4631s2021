const express = require('express')
const bodyParser = require('body-parser')
const users = require('./components/users')
const multer  = require('multer')
const server = express()
const path = require('path')
const multerConfig = require('./components/multerConfig')

const verbose = true;
function vprint(s){
  if(verbose){
    console.log(s);
  }
}

server.use(bodyParser.json());
server.use(bodyParser.urlencoded({
  extended: true
}));

server.get('/', (req, res) => {
  res.end("Hello World")
})

server.post('/createUser', (req, res) => {
  let response = {}
  response.status = '1'
  if("name" in req.body && "email" in req.body && "password" in req.body){
    if(req.body.name.length < 2){
      response.error = 'Name must contain at least one character.'
      res.header("Content-Type",'application/json')
      res.send(JSON.stringify(response, null, 4))
    } else if(req.body.email.length < 2){
      response.error = 'Email must contain at least two characters.'
      res.header("Content-Type",'application/json')
      res.send(JSON.stringify(response, null, 4))
    } else if(req.body.email.split('@').length != 2){
      response.error = 'Invalid email address.'
      res.header("Content-Type",'application/json')
      res.send(JSON.stringify(response, null, 4))
    } else if(req.body.password.length < 8 || req.body.password.length > 32){
      response.error = 'Password must contain at least 8 characters, and at most 32.'
      res.header("Content-Type",'application/json')
      res.send(JSON.stringify(response, null, 4))
    } else {
      users.createUser(req.body.name, req.body.email, req.body.password).then(result => {
        response.status = '0'
        response.result = result
        res.header("Content-Type",'application/json')
        res.send(JSON.stringify(response, null, 4))
      }).catch(err => {
        response.error = err.error
        res.header("Content-Type",'application/json')
        res.send(JSON.stringify(response, null, 4))
      })
    }
  } else {
    response.error = 'User must have name, email, and password.'
    res.header("Content-Type",'application/json')
    res.send(JSON.stringify(response, null, 4))
  }
})

server.post('/login', (req, res) => {
  let response = {}
  response.status = '1'
  if("email" in req.body && "password" in req.body){
    users.login(req.body.email, req.body.password).then(result => {
      response.status = '0'
      response.user = result
      vprint(result)
      res.header("Content-Type",'application/json')
      res.send(JSON.stringify(response, null, 4))
    }).catch(err => {
      response.error = err.error
      res.header("Content-Type",'application/json')
      res.send(JSON.stringify(response, null, 4))
    })
  } else {
    response.error = 'Login must have email and password.'
    res.header("Content-Type",'application/json')
    res.send(JSON.stringify(response, null, 4))
  }
})

server.post('/logout', (req, res) => {
  let response = {}
  response.status = '1'
  if('token' in req.body){
    users.logout(req.body.token).then(result => {
      response.status = '0'
      response.result = result
      res.header("Content-Type",'application/json')
      res.send(JSON.stringify(response, null, 4))
    }).catch(err => {
      response.error = err.error
      res.header("Content-Type",'application/json')
      res.send(JSON.stringify(response, null, 4))
    })
  } else {
    response.error = 'Token not supplied for logout'
    res.header("Content-Type",'application/json')
    res.send(JSON.stringify(response, null, 4))
  }
})

server.post('/upload', (req, res) => {
  multerConfig.upload(req, res, (err) => {
    if(err){
      vprint(err)
      res.end('error: '+err.error);
    } else {
      if(req.file == undefined){
        vprint(err)
        res.end('error: '+err.error);
      } else {
        res.end('File uploaded: '+req.file.filename)
      }
    }
  });
});

vprint("Listening on http://localhost:3000")
server.listen(3000)
