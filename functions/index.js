const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();
const jwt = require("jsonwebtoken");
const JWT_SECRET = "21D40CD42CCA9BE6B8932855781FC84A";

exports.sendPetition = functions.firestore
    .document("/users/{uid}/petitions/{id}").onCreate((snap, context) => {
      const petition = snap.data();
      const id = petition.id;
      const sender = petition.sender;
      const receiver = petition.receiver;
      const state = petition.state;
      const uid = context.params.uid;
      if (receiver != uid) {
        return admin.firestore().collection("users").doc(receiver)
            .collection("petitions").doc(id).set({id: id, sender: sender,
              receiver: receiver, state: state});
      }
      return null;
    });

exports.acceptPetition = functions.firestore
    .document("/users/{uid}/petitions/{id}").onUpdate((change, context) => {
      const accept = change.after.data();
      const sender = accept.sender;
      const newState = accept.state;
      const previousState = change.before.data().state;
      const id = context.params.id;
      if (previousState == "pending" && newState == "accepted") {
        return admin.firestore().collection("users").doc(sender)
            .collection("petitions").doc(id).update({state: "accepted"});
      }
      return null;
    });

exports.rejectPetition = functions.firestore
    .document("/users/{uid}/petitions/{id}").onUpdate((change, context) => {
      const reject = change.after.data();
      const sender = reject.sender;
      const newState = reject.state;
      const previousState = change.before.data().state;
      const id = context.params.id;
      if (previousState == "pending" && newState == "rejected") {
        return admin.firestore().collection("users").doc(sender)
            .collection("petitions").doc(id).update({state: "rejected"});
      }
      return null;
    });

exports.createRelative = functions.firestore
    .document("/users/{uidB}/relatives/{uidA}").onCreate((snap, context) => {
      const uidA = snap.data().uid;
      const uidB = context.params.uidB;
      admin.firestore().collection("users").doc(uidB).get().then((B) => {
        return admin.firestore().collection("users").doc(uidA)
            .collection("relatives").doc(B.get("uid"))
            .set({uid: B.get("uid"), name: B.get("name"), role: B.get("role"),
              email: B.get("email"), image: B.get("image")});
      });
      return null;
    });

exports.deleteRelative = functions.firestore
    .document("/users/{uidA}/relatives/{uidB}").onDelete((snap, context) => {
      const uidA = context.params.uidA;
      const uidB = snap.data().uid;
      admin.firestore().collection("users").doc(uidA).get().then((A) => {
        return admin.firestore().collection("users").doc(uidB)
            .collection("relatives").doc(A.get("uid")).delete();
      });
      return null;
    });

exports.createAlert = functions.firestore
    .document("/users/{uid}/alerts/{id}").onCreate((snap, context) => {
      const id = snap.data().id;
      const sender = snap.data().sender;
      const receiver = snap.data().receiver;
      const tag = snap.data().tag;
      const repetition = snap.data().repetition;
      const time = snap.data().time;
      const daysOfWeek = snap.data().daysOfWeek;
      const date = snap.data().date;
      return admin.firestore().collection("users").doc(receiver)
          .collection("alerts").doc(id).set({id: id, sender: sender,
            receiver: receiver, tag: tag, repetition: repetition,
            time: time, daysOfWeek: daysOfWeek, date: date});
    });

exports.deleteAlert = functions.firestore
    .document("users/{uid}/alerts/{id}").onDelete((snap, context) => {
      const id = snap.data().id;
      const receiver = snap.data().receiver;
      return admin.firestore().collection("users").doc(receiver)
          .collection("alerts").doc(id).delete();
    });

exports.updateAlert = functions.firestore
    .document("users/{uid}/alerts/{id}").onUpdate((change, context) => {
      const update = change.after.data();
      const sender = change.before.data().sender;
      const receiver = change.before.data().receiver;
      const tag = update.tag;
      const repetition = update.repetition;
      const time = update.time;
      const daysOfWeek = update.daysOfWeek;
      const date = update.date;
      const uid = context.params.uid;
      const id = context.params.id;
      if (receiver != uid) {
        return admin.firestore().collection("users").doc(receiver)
            .collection("alerts").doc(id).set({id: id, sender: sender,
              receiver: receiver, tag: tag, repetition: repetition,
              time: time, daysOfWeek: daysOfWeek, date: date});
      }
      return null;
    });

exports.createVideocall = functions.firestore
    .document("videocalls/{id}").onCreate((snap, context) => {
      const sender = snap.data().sender;
      const receiver = snap.data().receiver;
      const callId = context.params.id;
      admin.firestore().collection("users").doc(sender).get().then((s) => {
        const senderTjw = {
          "aud": "paulasoria",
          "iss": "paulasoria",
          "sub": "jitsi.paulasoria.tk",
          "room": callId,
          "exp": 1932477254,
          "moderator": true,
          "contex": {
            "user": {
              "image": s.get("image"),
              "name": s.get("name"),
              "email": s.get("email"),
              "uid": s.get("uid"),
            },
          },
        };
        jwt.sign(senderTjw, JWT_SECRET);
        const senderName = s.get("name");
        const senderEmail = s.get("email");
        const senderImage = s.get("image");
        admin.firestore().collection("users").doc(receiver).get().then((r) => {
          const receiverTjw = {
            "aud": "paulasoria",
            "iss": "paulasoria",
            "sub": "jitsi.paulasoria.tk",
            "room": callId,
            "exp": 1932477254,
            "moderator": true,
            "contex": {
              "user": {
                "image": r.get("image"),
                "name": r.get("name"),
                "email": r.get("email"),
                "uid": r.get("uid"),
              },
            },
          };
          jwt.sign(receiverTjw, JWT_SECRET);
          const receiverToken = r.get("token");
          const message = {
            data: {
              senderName: senderName,
              senderEmail: senderEmail,
              senderImage: senderImage,
              callId: callId,
              type: "incomingCall",
            },
            token: receiverToken,
          };
          admin.messaging().send(message).then((response) => {
            console.log("Successfully sent message: ", response);
          }).catch((error) => {
            console.log("Error sending message: ", error);
          });
          return senderTjw;
        });
      });
    });

exports.rejectVideocall = functions.firestore
    .document("videocalls/{id}").onUpdate((change, context) => {
      const reject = change.after.data();
      const newState = reject.state;
      const sender = reject.sender;
      const previousState = change.before.data().state;
      const callId = context.params.id;

      admin.firestore().collection("users").doc(sender).get().then((s) => {
        const senderName = s.get("name");
        const senderEmail = s.get("email");
        const senderToken = s.get("token");
        if (previousState == "waiting" && newState == "rejected") {
          const message = {
            data: {
              senderName: senderName,
              senderEmail: senderEmail,
              callId: callId,
              type: "rejectedCall",
            },
            token: senderToken,
          };
          admin.messaging().send(message).then((response) => {
            console.log("Successfully sent message: ", response);
          }).catch((error) => {
            console.log("Error sending message: ", error);
          });
          return "ok";
        }
        return null;
      });
    });

exports.acceptVideocall = functions.firestore
    .document("videocalls/{id}").onUpdate((change, context) => {
      const accept = change.after.data();
      const newState = accept.state;
      const sender = accept.sender;
      const previousState = change.before.data().state;
      const callId = context.params.id;

      admin.firestore().collection("users").doc(sender).get().then((s) => {
        const senderToken = s.get("token");
        if (previousState == "waiting" && newState == "accepted") {
          const message = {
            data: {
              callId: callId,
              type: "acceptedCall",
            },
            token: senderToken,
          };
          admin.messaging().send(message).then((response) => {
            console.log("Successfully sent message: ", response);
          }).catch((error) => {
            console.log("Error sending message: ", error);
          });
          return "ok";
        }
        return null;
      });
    });
