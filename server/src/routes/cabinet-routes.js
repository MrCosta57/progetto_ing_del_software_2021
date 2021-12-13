/* By Black Buffalos */
//https://ozenero.com/nodejs-use-mongoose-to-save-files-images-to-mongodb

require('dotenv').config();
const config = require('config');
const db_config = process.env[config.get('dbConfig.host')];
//console.log(db_config);
const util = require("util");
const multer = require("multer");
const { GridFsStorage } = require("multer-gridfs-storage");
const express = require('express')
const router = new express.Router()

const MongoClient = require("mongodb").MongoClient;
const GridFSBucket = require("mongodb").GridFSBucket;
const url = db_config.substring(0, db_config.lastIndexOf("/")+1);
//console.log(url);
const mongoClient = new MongoClient(url);
const bucket_name = 'cabinet_bucket';

const Group = require('../models/group');

/*
//For file uploading testing
const path = require("path");
router.get("/",(req, res, next) => {
  return res.sendFile(path.join(`${__dirname}/../routes/test_fileUpload.html`));
});*/

//Post file in a group cabinet (id: group_id)
router.post("/:id", async (req, res, next) => {
  if (!req.user_id) {
    return res.status(401).send('Not authenticated')
  }
  const group_id = req.params.id;
  const creator_id=req.user_id;
  if (!await Group.findOne({ group_id })) {
    return res.status(404).send('Non existing group')
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
          metadata: { 'group_id': group_id, 'creator_id': creator_id }
        };
      }
    });
    let uploadFiles = multer({ storage: storage }).single("file");
    let uploadFilesMiddleware = util.promisify(uploadFiles);
    
    
    await uploadFilesMiddleware(req, res);
    console.log(req.file);

    if (req.file == undefined) {
      return res.send({
        message: "You must select a file.",
      });
    }

    return res.send({
      message: "File has been uploaded.",
    });

  } catch (error) {
    console.log(error);
    next(error);
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
  /*if (!req.user_id) {
    return res.status(401).send('Not authenticated')
  }
  const group_id = req.params.id;
  if (!await Group.findOne({ group_id })) {
    return res.status(404).send('Non existing group')
  }*/

  try {
    await mongoClient.connect();

    const database = mongoClient.db(db_config.split("/").pop()); //extract the database name from string
    const files = database.collection(bucket_name + ".files");

    //TODO: get dei file per gruppo
    const cursor = files.find({});

    if ((await cursor.count()) === 0) {
      return res.status(500).send({
        message: "No files found!",
      });
    }

    let fileInfos = [];
    await cursor.forEach((doc) => {
      fileInfos.push({
        name: doc.filename,
        //TODO: mettere le varie info utili (togliere "url")
        url: "http://localhost:4000/files/" + doc.filename,
      });
    });

    return res.status(200).send(fileInfos);
  } catch (error) {
    return res.status(500).send({
      message: error.message,
    });
  }
});


//Get file in a group by id
router.get("/:group_id/:file_id", async (req, res) => {

  if (!await Group.findOne({ group_id })) {
    return res.status(404).send('Non existing group')
  }
  //TODO: check del id del file
  try {
    await mongoClient.connect();

    const database = mongoClient.db(db_config.split("/").pop()); //extract the database name from string
    const bucket = new GridFSBucket(database, {
      bucketName: bucket_name,
    });

    let downloadStream = bucket.openDownloadStreamByName(req.params.name);

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
    return res.status(500).send({
      message: error.message,
    });
  }
});


//TODO Delete file in a group by id
router.delete('/cabinet/:group_id/:file_id', async (req, res, next) => {
  if (!req.user_id) {
    return res.status(401).send('Not authenticated')
  }
  const group_id = req.params.group_id;
  const file_id = req.params.file_id;
  try {
    await SingleFile.deleteOne();
    res.send("File successfully deleted");
  } catch (error) {
    next(error)
    //console.log(error);
    //res.send("An error occured");
  }
});


module.exports = router;