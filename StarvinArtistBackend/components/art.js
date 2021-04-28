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
Art object:
{
  "filename": "<filename>",
  "name": "<name>",
  "owner":  "<userid>",
  "tags": ["tag","tag","tag"],
  "artid": "uniqid generated"
}
*/

/*
Tag object:
{
  "tag": "<tag>",
  "photos": ['artid', 'artid', 'artid']
}
*/

function addPhoto(filename, name, owner, tags) {
  return new Promise(function(resolve, reject) {
    vprint('adding photo: '+filename+' '+name+' '+owner+' '+tags)
    var photos = null
    var tagsCollection = null
    var artObj = null
    var client = null
    connection.then(res => {
      client = res
      return client.db(dbname).collection('photos')
    }).then(res => {
      photos = res
      artObj = {}
      artObj.filename = filename
      artObj.name = name
      artObj.owner = owner
      artObj.tags = tags
      artObj.artid = uuidv4()
      return photos.insertOne(artObj)
    }).then(res => {
      return client.db(dbname).collection('tags')
    }).then(res => {
      tagsCollection = res
      return Promise.all(
        tags.map(tag => {
          tagsCollection.findOne({'tag': tag}).then(res => {
            if(res == null){
              return tagsCollection.insertOne({'tag': tag, 'photos': [artObj.artid]})
            } else {
              return tagsCollection.findOneAndUpdate({'tag': tag}, {'$push': {'photos': artObj.artid}})
            }
          })
        })
      )
    }).then(res => {
      return client.db(dbname).collection('users')
    }).then(users => {
      return users.findOneAndUpdate({'userid': owner}, {'$push': {'art': artObj.artid}})
    }).then(res => {
      resolve(artObj)
    }).catch(err => {
      vprint(err)
      reject(err)
    })
  });
}

exports.addPhoto = addPhoto
