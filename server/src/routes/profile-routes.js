const express = require('express')
const router = new express.Router()
const jwt = require('jsonwebtoken')
const Profile = require('../models/profile')
const Child=require('../models/child')
const Parent=require('../models/parent')
const Image = require('../models/image')

router.get('/', (req, res, next) => {
  if (!req.user_id) { return res.status(401).send('Not authenticated') }
  const searchBy = req.query.searchBy
  switch (searchBy) {
    case 'ids':
      const { ids } = req.query
      if (!ids) {
        return res.status(400).send('Bad Request')
      }
      Profile.find({ user_id: { $in: ids } })
        .populate('image')
        .lean()
        .exec()
        .then(profiles => {
          if (profiles.length === 0) {
            return res.status(404).send('Profiles not found')
          }
          for (const profile of profiles) {
            if (profile.image === null) {
              Image.create({
                owner_id: profile.user_id,
                image_id: profile.image_id,
                thumbnail_path: '/images/profiles/user_default_photo.png',
                owner_type: 'user',
                path: '/images/profiles/user_default_photo.png'
              })
              profile.image = {
                thumbnail_path: '/images/profiles/user_default_photo.png',
                path: '/images/profiles/user_default_photo.png'
              }
            }
          }
          res.json(profiles)
        }).catch(next)
      break
    case 'visibility':
      const { visible } = req.query
      if (!visible) {
        return res.status(400).send('Bad Request')
      }
      Profile.find({ visible, suspended: false })
        .populate('image')
        .sort({ given_name: 1, family_name: 1 })
        .lean()
        .exec()
        .then(profiles => {
          if (profiles.length === 0) {
            return res.status(404).send('Profiles not found')
          }
          for (const profile of profiles) {
            if (profile.image === null) {
              Image.create({
                owner_id: profile.user_id,
                image_id: profile.image_id,
                thumbnail_path: '/images/profiles/user_default_photo.png',
                owner_type: 'user',
                path: '/images/profiles/user_default_photo.png'
              })
              profile.image = {
                thumbnail_path: '/images/profiles/user_default_photo.png',
                path: '/images/profiles/user_default_photo.png'
              }
            }
          }
          res.json(profiles)
        }).catch(next)
      break
    default:
      res.status(400).send('Bad Request')
  }
})

router.post('/change_greenpass_available', async (req, res, next) => {  //Usato req.query !!!
  if (!req.user_id) { return res.status(401).send('Not authorized') }
  try {
    const user_id = req.user_id
    const greenpass_available = req.query.greenpass_available
    const profile = await Profile.findOne({ user_id: user_id })
    if (profile.suspended) {
      await Profile.updateOne({ user_id }, { suspended: false })
      const usersChildren = await Parent.find({ parent_id: user_id })
      const childIds = usersChildren.map(usersChildren.child_id)
      await Child.updateMany({ child_id: { $in: childIds } }, { suspended: false })
    }
    const token = await jwt.sign({ user_id, greenpass_available }, process.env.SERVER_SECRET)
    const response = {
      id: user_id,
      greenpass_available,
      token
    }
    profile.greenpass_available = req.query.greenpass_available
    await profile.save()
    res.json(response)
  } catch (error) {
    next(error)
  }
})

router.post('/change_is_positive_state', async (req, res, next) => {  //Usato req.query !!!
  if (!req.user_id) { return res.status(401).send('Not authorized') }
  try {
    const user_id = req.user_id
    const is_positive = req.query.is_positive
    const profile = await Profile.findOne({ user_id: user_id })
    if (profile.suspended) {
      await Profile.updateOne({ user_id }, { suspended: false })
      const usersChildren = await Parent.find({ parent_id: user_id })
      const childIds = usersChildren.map(usersChildren.child_id)
      await Child.updateMany({ child_id: { $in: childIds } }, { suspended: false })
  }
  const token = await jwt.sign({ user_id, is_positive }, process.env.SERVER_SECRET)
  const response = {
    id: user_id,
    is_positive,
    token
  }
  profile.is_positive = req.query.is_positive
  await profile.save()
  res.json(response)
  } catch (error) {
    next(error)
  }
})



router.post('/change_childs_is_positive_state', async (req, res, next) => {  //Usato req.query !!!
  if (!req.user_id) { return res.status(401).send('Not authorized') }
  try {
    const parent_id = req.user_id
    const child_id = req.query.child_id
    const is_positive = req.query.is_positive

    console.log("Parent_id: " + parent_id)
    console.log("Child_id: " + child_id)
    if (await Parent.find({parent_id: parent_id, child_id: child_id})){
      return res.status(500).send('Users not found');
    }

    const child = await Child.findOne({child_id: child_id});
    if (child.suspended) {
      await Profile.updateOne({ parent_id }, { suspended: false })
      const usersChildren = await Parent.find({ parent_id: user_id })
      const childIds = usersChildren.map(usersChildren.child_id)
      await Child.updateMany({ child_id: { $in: childIds } }, { suspended: false })
  }
  const token = await jwt.sign({ child_id, is_positive }, process.env.SERVER_SECRET)
  const response = {
    id: child_id,
    is_positive,
    token
  }
  child.is_positive = is_positive
  await child.save()
  res.json(response)
  } catch (error) {
    next(error)
  }
})



module.exports = router
