const admin = require("firebase-admin");

const serviceAccount = require("./seniorcare-tfg-firebase-adminsdk-j0gbh-3e97499306.json");
admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://seniorcare-tfg.firebaseio.com"
});


const db = admin.firestore();
const CronJob = require("cron").CronJob;
const job = new CronJob("* * * * *", function() {
    getActualAlarms();
}, null, true, "America/Los_Angeles");
job.start();


async function getActualAlarms(){
    const today = new Date();
    const actualTime = today.getHours()+":"+today.getMinutes();
    const actualDate =  today.getDate()+"/"+(today.getMonth()+1)+"/"+today.getFullYear();
    const timestamp = today.getTime();

    let usersArray = [];
    db.collection('users').get().then(users => {
        users.forEach( user => {
            const userInfo = user.data();
            usersArray.push(userInfo);
        })
        for(let i = 0; i < usersArray.length; i++){
            db.collection('users').doc(usersArray[i].uid).collection('alerts').get().then(alerts => {
                alerts.forEach( alert => {
                    const alertInfo = alert.data();
                    if(alertInfo.repetition == "weekly"){
                        const valueActualDayOfWeek = getValueActualDayOfWeek(today.getDay(), alertInfo);
                        if(alertInfo.time == actualTime && valueActualDayOfWeek == 1){
                            if(usersArray[i].uid == alertInfo.receiver){
                                sendMessage(usersArray[i].token, alertInfo.tag, actualTime, actualDate, "alert");
                                putAlertOnHistory(alertInfo.sender, usersArray[i].name, alertInfo, actualTime, actualDate, timestamp);
                            }
                        }
                    } else {    //eventually
                        if(alertInfo.time == actualTime && alertInfo.date == actualDate){
                            if(usersArray[i].uid == alertInfo.receiver){
                                sendMessage(usersArray[i].token, alertInfo.tag, actualTime, actualDate, "alert");
                                putAlertOnHistory(alertInfo.sender, usersArray[i].name, alertInfo, actualTime, actualDate, timestamp);
                            }
                        }
                    }
                })
            }).catch((error) => {
                console.log("Error getting alerts from database: ", error);
            });      
        }
    }).catch((error) => {
        console.log("Error getting users from database: ", error);
    });
}

 
async function putAlertOnHistory(idSender, receiverName, alert, actualTime, actualDate, timestamp){
    db.collection("users").doc(idSender).collection("historyAlerts").doc()
    .set({id: alert.id, tag: alert.tag, receiver: receiverName, time: actualTime, date: actualDate, timestamp: timestamp})
    .then(snapshot => {
        console.log("Successfully write alert on history: ", snapshot);
    }).catch((error) => {
        console.log("Error writing alert on history: ", error);
    });
}


function sendMessage(token, msg, time, date, type){
    const message = {
        data: {
            msg: msg,
            time: time,
            date: date,
            type: type
        },
        token: token
    };
    console.log(message);
    admin.messaging().send(message).then((response) => {
        console.log("Successfully sent message: ",response);
    }).catch((error) => {
        console.log("Error sending message: ", error);
    });
}


function getValueActualDayOfWeek(day, alertInfo){
    switch(day){
        case 0:
            return alertInfo.daysOfWeek.D;
        case 1:
            return alertInfo.daysOfWeek.L;
        case 2:
            return alertInfo.daysOfWeek.M;
        case 3:
            return alertInfo.daysOfWeek.X;
        case 4:
            return alertInfo.daysOfWeek.J;
        case 5:
            return alertInfo.daysOfWeek.V;
        case 6:
            return alertInfo.daysOfWeek.S;
    }
}