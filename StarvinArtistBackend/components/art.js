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
  "artid": "uniqid generated",
  "price" : float,
  "contact": "<contact>"
}
*/

/*
Tag object:
{
  "tag": "<tag>",
  "photos": ['artid', 'artid', 'artid']
}
*/

function addPhoto(filename, name, owner, tags, price, contact) {
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
      artObj.price = price
      artObj.contact = contact
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

function getTagPhoto(tag, index) {
  return new Promise(function(resolve, reject) {
    var client = null
    var artid = null
    connection.then(res => {
      client = res
      return client.db(dbname).collection('tags')
    }).then(tags => {
      return tags.findOne({'tag': tag})
    }).then(tag => {
      if(tag != null){
        return tag.photos[index%(tag.photos.length)]
      } else {
        reject({'error': 'No tag found'})
        return new Error('No tag found')
      }
    }).then(res => {
      artid = res
      return client.db(dbname).collection('photos')
    }).then(photos => {
      return photos.findOne({'artid': artid})
    }).then(photo => {
      vprint('found photo')
      resolve(photo.filename)
    }).catch(err => {
      vprint(err)
      reject(err)
    })
  });
}

function getPhoto(index){
  return new Promise(function(resolve, reject) {
    connection.then(client => {
      return client.db(dbname).collection('photos')
    }).then(photos => {
      return photos.find().toArray()
    }).then(photosArray => {
      return photosArray[index%(photosArray.length)]
    }).then(photo => {
      resolve(photo.filename)
    }).catch(err => {
      vprint(err)
      reject(err)
    })
  });
}

function getPhotoInfo(index){
  return new Promise(function(resolve, reject) {
    connection.then(client => {
      return client.db(dbname).collection('photos')
    }).then(photos => {
      return photos.find().toArray()
    }).then(photosArray => {
      resolve(photosArray[index%(photosArray.length)])
    }).catch(err => {
      vprint(err)
      reject(err)
    })
  });
}

function getTagPhotoInfo(tag, index) {
  return new Promise(function(resolve, reject) {
    var client = null
    var artid = null
    connection.then(res => {
      client = res
      return client.db(dbname).collection('tags')
    }).then(tags => {
      return tags.findOne({'tag': tag})
    }).then(tag => {
      if(tag != null){
        return tag.photos[index%(tag.photos.length)]
      } else {
        reject({'error': 'No tag found'})
        return new Error('No tag found')
      }
    }).then(res => {
      artid = res
      return client.db(dbname).collection('photos')
    }).then(photos => {
      return photos.findOne({'artid': artid})
    }).then(photo => {
      vprint('found photo')
      resolve(photo)
    }).catch(err => {
      vprint(err)
      reject(err)
    })
  });
}

exports.addPhoto = addPhoto
exports.getTagPhoto = getTagPhoto
exports.getPhoto = getPhoto
exports.getPhotoInfo = getPhotoInfo
exports.getTagPhotoInfo = getTagPhotoInfo
