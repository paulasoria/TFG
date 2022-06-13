const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

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

exports.addNewRelative = functions.firestore
    .document("/users/{uidB}/relatives/{uidA}").onCreate((snap, context) => {
      const uidA = snap.data().uid;
      const uidB = context.params.uidB;
      admin.firestore().collection("users").doc(uidB).get().then((B) => {
        return admin.firestore().collection("users").doc(uidA)
            .collection("relatives").doc(B.get("uid"))
            .set({uid: B.get("uid"), name: B.get("name"),
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

exports.addNewAlert = functions.firestore
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
