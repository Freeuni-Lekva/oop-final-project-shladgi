import { loadSessionValue } from "./getSessionInfo.js";


let currInterval =0;
let userid = 0;


document.addEventListener("DOMContentLoaded", async () => {

   let sm = document.getElementById("sm");
   console.log(sm);

    userid = await loadSessionValue("userid");
    getNotes();




});



function getNotes(){
currInterval++;
console.log(currInterval);
    fetch("getNotes",
        {method : "POST",
            headers :{
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body : new URLSearchParams({
                userid :  userid,
                interval : currInterval
            })
        }
    ).then(res => res.json())
        .then(data => {
           if(data.success){

               let notes =  document.getElementById("notes");
               data.info.forEach(n=> {
                  notes.innerHTML+= `<div class="note ${n.viewed ? "" : "viewed"}">
                                    <span>${n.senderName}</span><span>${new Date(n.createDate).toLocaleDateString("en-GB")}</span> 
                                    <p>${n.text}</p></div>`;
               })
               if (data.info.length < 10) {
                   document.getElementById("sm").style.display = "none";
               }
           }
            }
        ).catch(err=> {
            console.log(err);
    });
}



function getChallenges(interval, userid){
    fetch("getChallanges",
        {method : "POST",
            headers :{
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body : new URLSearchParams({
                userid :  userid,
                interval : interval
            })
        }
    ).then(res => res.json())
        .then(data => {
            if(data.success){
                let notes =  document.getElementById("challenges");
                data.info.forEach(c=> {
                    notes.innerHTML+= `<div ${!c.viewed ? "": "class=\" viewed\"" }>
                                    <span>${c.senderName}</span><span>${new Date(c.createDate).toLocaleDateString("en-GB")}</span> 
                                    <p>${c.text}</p></div>`;
                })
            }

            }

        ).catch(err=> {
        console.log(err);
    });
}