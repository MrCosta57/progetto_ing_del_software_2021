/* By Black Buffalos */
//https://ozenero.com/nodejs-use-mongoose-to-save-files-images-to-mongodb

require('dotenv').config();
const config = require('config');
const db_config = process.env[config.get('dbConfig.host')];
const util = require("util");
const multer = require("multer");
const { GridFsStorage } = require("multer-gridfs-storage");
const express = require('express')
const router = new express.Router()

const MongoClient = require("mongodb").MongoClient;
const GridFSBucket = require("mongodb").GridFSBucket;
const ObjectID = require('mongodb').ObjectID;
const url = db_config.substring(0, db_config.lastIndexOf("/") + 1);
const mongoClient = new MongoClient(url);
const bucket_name = 'cabinet_bucket';

const Profile = require('../models/profile');
const Member = require('../models/member');


//For file uploading testing
/*const path = require("path");
router.get("/",(req, res, next) => {
  return res.sendFile(path.join(`${__dirname}/../routes/test_fileUpload.html`));
});*/

//Post file in a group cabinet (id: group_id)
router.post("/:id", async (req, res, next) => {
  let creator_id = req.user_id;
  if (!creator_id) {
    return res.status(401).send('Not authenticated')
  }
  creator_id = req.user_id + '';
  const group_id = req.params.id;

  const member = await Member.findOne({
    group_id,
    creator_id,
    group_accepted: true,
    user_accepted: true
  })
  if (!member) {
    return res.status(401).send('Unauthorized')
  }

  try {
    let storage = new GridFsStorage({
      url: db_config,
      options: { useNewUrlParser: true, useUnifiedTopology: true },
      file: (req, file) => {
        const match = ['image/png', 'image/jpg', 'image/jpeg', 'audio/aac', 'audio/mpeg', 'video/mp4', 'text/plain',
          'application/vnd.openxmlformats-officedocument.presentationml.presentation',
          'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
          'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
          'application/zip', 'image/gif', 'audio/wav', 'application/pdf', 'video/x-msvideo'];

        if (match.indexOf(file.mimetype) === -1) {
          const filename = `${Date.now()}-${file.originalname}`;
          return filename;
        }

        return {
          bucketName: 'cabinet_bucket',
          filename: `${Date.now()}-${file.originalname}`,
          metadata: { 'group_id': group_id, 'creator_id': creator_id, description: req.description }
        };
      }
    });
    let uploadFiles = multer({ storage: storage }).single("file");
    let uploadFilesMiddleware = util.promisify(uploadFiles);

    await uploadFilesMiddleware(req, res);
    //console.log(req.file);

    if (req.file == undefined) {
      return res.send({
        message: "You must select a file.",
      });
    }

    return res.send({
      message: "File has been uploaded.",
    });

  } catch (error) {

    if (error.code === "LIMIT_UNEXPECTED_FILE") {
      return res.status(400).send({
        message: "Too many files to upload.",
      });
    }

    return res.send({
      message: "Error when trying upload image: ${error}",
    });
  }
});


//Get all files info in a group (id: group_id) (not entire files)
router.get("/:id", async (req, res) => {
  const user_id = req.user_id;
  if (!user_id) {
    return res.status(401).send('Not authenticated')
  }
  const group_id = req.params.id;
  const member = await Member.findOne({
    group_id,
    user_id,
    group_accepted: true,
    user_accepted: true
  })
  if (!member) {
    return res.status(401).send('Unauthorized')
  }

  try {
    await mongoClient.connect();

    const database = mongoClient.db(db_config.split("/").pop()); //extract the database name from string
    const files = database.collection(bucket_name + ".files");

    const cursor = files.find({ 'metadata.group_id': group_id });

    if ((await cursor.count()) === 0) {
      return res.status(500).send({
        message: "No files found!",
      });
    }

    let fileInfos = [];
    await cursor.forEach(async (doc) => {
      let creator_id = doc.metadata.creator_id;
      let user_info = await Profile.findOne({ user_id: creator_id });
      
      fileInfos.push({
        file_id: doc._id,
        name: doc.filename,
        date: doc.uploadDate,
        contentType: doc.contentType,
        creator_name: user_info.given_name
      });
    });

    return res.status(200).send(fileInfos);
  } catch (error) {
    next(error);
  }
});


//Get file in a group by id
router.get("/:group_id/:file_id", async (req, res) => {
  const user_id = req.user_id;
  const group_id = req.params.id;
  const file_id = req.params.file_id;
  if (!user_id) {
    return res.status(401).send('Not authenticated')
  }
  const member = await Member.findOne({
    group_id,
    user_id,
    group_accepted: true,
    user_accepted: true
  })
  if (!member) {
    return res.status(401).send('Unauthorized')
  }

  try {
    await mongoClient.connect();

    const database = mongoClient.db(db_config.split("/").pop()); //extract the database name from string
    const files = database.collection(bucket_name + ".files");
    const cursor = files.find({ _id: ObjectID(file_id) });
    
    if ((await cursor.count()) === 0) {
      return res.status(500).send({
        message: "No files found!",
      });
    }

    const bucket = new GridFSBucket(database, {
      bucketName: bucket_name,
    });

    let downloadStream = bucket.openDownloadStream(ObjectID(file_id));

    downloadStream.on("data", function (data) {
      return res.status(200).write(data);
    });

    downloadStream.on("error", function (err) {
      return res.status(404).send({ message: "Cannot download the Image!" });
    });

    downloadStream.on("end", () => {
      return res.end();
    });

  } catch (error) {
    next(error);
  }
});


router.delete('/:group_id/:file_id', async (req, res, next) => {
  let user_id = req.user_id;
  const group_id = req.params.group_id;
  const file_id = req.params.file_id;
  if (!user_id) {
    return res.status(401).send('Not authenticated')
  }
  user_id = req.user_id + '';
  const member = await Member.findOne({
    group_id,
    user_id,
    group_accepted: true,
    user_accepted: true
  })
  if (!member) {
    return res.status(401).send('Unauthorized')
  }

  try {
    await mongoClient.connect();

    const database = mongoClient.db(db_config.split("/").pop()); //extract the database name from string
    const files = database.collection(bucket_name + ".files");

    const cursor = files.find({ _id: ObjectID(file_id), 'metadata.group_id': group_id, 'metadata.creator_id': user_id });

    if ((await cursor.count()) === 0) {
      return res.status(500).send({
        message: "No files found!",
      });
    }

    const bucket = new GridFSBucket(database, {
      bucketName: bucket_name,
    });

    bucket.delete(ObjectID(file_id));
    res.send("File successfully deleted");

  } catch (error) {
    next(error);
  }

});


async function deleteAll_files(group_id) {
  //Delete all files for the group
  try {
    await mongoClient.connect();
    const database = mongoClient.db(db_config.split("/").pop()); //extract the database name from string
    const files = database.collection(bucket_name + ".files");
    const cursor = files.find({ 'metadata.group_id': group_id });

    if ((await cursor.count()) === 0) {
      return res.status(500).send({
        message: "No files found!",
      });
    }
    await cursor.forEach((doc) => {
      bucket.delete(ObjectID(doc._id));
    });
    res.send("Files successfully deleted");
  } catch (error) {
    next(error);
  }
}


module.exports = { router, deleteAll_files };