const express = require('express')
const router = new express.Router()
const jwt = require('jsonwebtoken')
const Profile = require('../models/profile')
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
  if (!req.query.user_id) { return res.status(401).send('Not authorized') }
  try {
    const user_id = req.query.user_id
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

router.post('/change_positivity_state', async (req, res, next) => {  //Usato req.query !!!
  if (!req.query.user_id) { return res.status(401).send('Not authorized') }
  try {
    const user_id = req.query.user_id
    const positivity = req.query.positivity
    const profile = await Profile.findOne({ user_id: user_id })
    if (profile.suspended) {
      await Profile.updateOne({ user_id }, { suspended: false })
      const usersChildren = await Parent.find({ parent_id: user_id })
      const childIds = usersChildren.map(usersChildren.child_id)
      await Child.updateMany({ child_id: { $in: childIds } }, { suspended: false })
  }
  const token = await jwt.sign({ user_id, positivity }, process.env.SERVER_SECRET)
  const response = {
    id: user_id,
    positivity,
    token
  }
  profile.positivity = req.query.positivity
  await profile.save()
  res.json(response)
  } catch (error) {
    next(error)
  }
})



module.exports = router
