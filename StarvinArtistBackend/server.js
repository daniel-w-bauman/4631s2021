const express = require('express')
const bodyParser = require('body-parser')
const users = require('./components/users')
const multer  = require('multer')
const server = express()
const path = require('path')
const multerConfig = require('./components/multerConfig')
const art = require('./components/art')

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
  let response = {}
  response.status = '1'
  vprint('Got upload request')
  multerConfig.upload(req, res, (err) => {
    if(err){
      vprint(err)
      response.error = err.error
      res.header("Content-Type",'application/json')
      res.send(JSON.stringify(response, null, 4))
    } else {
      if(req.file == undefined){
        vprint('Error: undefined file')
        response.error = 'Error: undefined file'
        res.header("Content-Type",'application/json')
        res.send(JSON.stringify(response, null, 4))
      } else {
        vprint('uploaded')
        if('token' in req.body && 'name' in req.body && 'tags' in req.body){
          vprint('Adding photo')
          users.getUser(req.body.token).then(user => {
            if(user != null){
              vprint('Adding for user '+user.name)
              art.addPhoto(req.file.filename, req.body.name, user.userid, req.body.tags.split(',')).then(result => {
                vprint(result)
                response.status = '0'
                response.result = 'Uploaded picture'
                res.header("Content-Type",'application/json')
                res.send(JSON.stringify(response, null, 4))
              }).catch(err => {
                vprint(err)
                response.error = err.error
                res.header("Content-Type",'application/json')
                res.send(JSON.stringify(response, null, 4))
              })
            } else {
              vprint("Invalid login.")
              response.error = "Invalid login."
              res.header("Content-Type",'application/json')
              res.send(JSON.stringify(response, null, 4))
            }
          }).catch(err => {
            vprint(err)
            response.error = err.error
            res.header("Content-Type",'application/json')
            res.send(JSON.stringify(response, null, 4))
          })
        } else {
          vprint('Token, name, or tags not in upload.')
          response.error = 'Token, name, or tags not in upload.'
          res.header("Content-Type",'application/json')
          res.send(JSON.stringify(response, null, 4))
        }
      }
    }
  })
})

server.get('/tag/:tag/:index', (req, res) => {
  if('tag' in req.params && 'index' in req.params){
    art.getTagPhoto(req.params.tag, Math.floor(req.params.index)).then(filename => {
      res.sendFile(__dirname+'/art/'+filename)
    }).catch(err => {
      vprint(err)
      res.end(err.error)
    })
  } else {
    res.end('No tag or index provided')
  }
})

server.get('/photo/:index', (req, res) => {
  if('index' in req.params){
    vprint("Requested photo #"+req.params.index);
    art.getPhoto(Math.floor(req.params.index)).then(filename => {
      vprint("Sending photo")
      res.sendFile(__dirname+'/art/'+filename)
    }).catch(err => {
      vprint(err)
      res.end(err.error)
    })
  } else {
    res.end('No index provided')
  }
})

vprint("Listening on http://localhost:3000")
server.listen(3000)
