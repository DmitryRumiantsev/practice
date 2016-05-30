

var Application = {
    mainUrl : 'http://localhost:8080/chat',
    messageList:[],
    token : 'TN11EN',
    name:"Anonymous",
    isDeleting:false,
    isEditing:false
};
function run(){
	var appContainer = document.getElementsByClassName('appContainer')[0];

	appContainer.addEventListener('click', delegateEvent);
	appContainer.addEventListener('change', delegateEvent);
	 Application.name=loadName()|| 'Anonymous';
    loadMessages(function(){render(Application.messageList);});
    document.getElementsByClassName("serverError")[0].style.display = "none";
}

function render(messages) {
	for(var i = 0; i < messages.length; i++) {
		renderMessage(messages[i]);
	}
}
function loadMessages(done) {
    var url = Application.mainUrl + '?token=' + Application.token;

    ajax('GET', url, null, function(responseText){
        var response = JSON.parse(responseText);

        Application.messageList = response.messages;
        Application.token = response.token;
        done();
    });
}
function loadName() {
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	var username= localStorage.getItem("Username");

	return username && JSON.parse(username);
}
function saveName(nameToSave) {
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	localStorage.setItem("Username", JSON.stringify(nameToSave));
}
function saveMessages(listToSave) {
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	localStorage.setItem("Application.messageList", JSON.stringify(listToSave));
}
function renderMessageType(element, message){
	element.classList.add(message.type);
	element.firstElementChild.childNodes[0].appendChild(document.createTextNode(message.author));
	element.firstElementChild.childNodes[1].appendChild(document.createTextNode(message.timestamp));
	element.firstElementChild.appendChild(document.createTextNode(message.text));
	element.setAttribute('message-id', message.id);
}

function renderMessage(message){
	var messages = document.getElementsByClassName('viewArea')[0];
	var element = elementFromTemplate();

	renderMessageType(element, message);
	messages.appendChild(element);
}

function elementFromTemplate() {
	var template = document.getElementById("message-template");
	return template.firstElementChild.cloneNode(true);
}
function newMessage(text,type) {
	return {
		id: '' + uniqueId(),
        author: ''+Application.name,
        timestamp: ''+ formDate().toGMTString(),
        text:text,
        type:'yourMessage'
	};
}
function delegateEvent(evtObj) {
	if(evtObj.type === 'click'){
	if((evtObj.target.classList.contains('sendMessage')||evtObj.target.classList.contains('send'))){
    		onAddButtonClick(evtObj,function(){render(Application.messageList);});
    	}
	if(evtObj.target.classList.contains('changeName')){
	    onChangeUsername(evtObj);
	}
	if((evtObj.target.classList.contains('removeMessage')||evtObj.target.parentElement.classList.contains('removeMessage'))){
    		onStartRemoving(evtObj);
    	}
    if((evtObj.target.classList.contains('editMessage')||evtObj.target.parentElement.classList.contains('editMessage'))){
        		onStartEditing(evtObj);
        	}

    if(Application.isDeleting){
    	if(((evtObj.target.classList.contains('yourMessage')||evtObj.target.classList.contains('yourMessageChanged')))){
                var messageToRemove=evtObj.target;
                onRemoveMessage(messageToRemove);
            }
        else if((evtObj.target.parentElement.classList.contains('yourMessage')||evtObj.target.parentElement.classList.contains('yourMessageChanged'))){
            var messageToRemove=evtObj.target.parentElement;
            onRemoveMessage(messageToRemove);
            }

    	else if((evtObj.target.parentElement.parentElement.classList.contains('yourMessage')||evtObj.target.parentElement.parentElement.classList.contains('yourMessageChanged'))){
                    var messageToRemove=evtObj.target.parentElement.parentElement;
                    onRemoveMessage(messageToRemove);
            	}
    }
     if(Application.isEditing){
        	if(((evtObj.target.classList.contains('yourMessage')||evtObj.target.classList.contains('yourMessageChanged')))){
                    var messageToEdit=evtObj.target;
                    onEditMessage(messageToEdit);
                }
            else if((evtObj.target.parentElement.classList.contains('yourMessage')||evtObj.target.parentElement.classList.contains('yourMessageChanged'))){
                var messageToEdit=evtObj.target.parentElement;
                onEditMessage(messageToEdit);
                }

        	else if((evtObj.target.parentElement.parentElement.classList.contains('yourMessage')||evtObj.target.parentElement.parentElement.classList.contains('yourMessageChanged'))){
                        var messageToEdit=evtObj.target.parentElement.parentElement;
                        onEditMessage(messageToEdit);
                	}
        }
}
}
function isInMessage(evtObj,className)
{
    return ((evtObj.target.classList.contains('yourMessage')||evtObj.target.classList.contains('yourMessageChanged'))||(evtObj.target.parentElement.classList.contains('yourMessage')||evtObj.target.parentElement.classList.contains('yourMessageChanged'))||(evtObj.target.parentElement.parentElement.classList.contains('yourMessage')||evtObj.target.parentElement.parentElement.classList.contains('yourMessageChanged')));
}
function onChangeUsername(){
	var result=prompt("Input userApplication.name",Application.name);
	if(result){
        Application.name=result;
        reassociateMessages(Application.name,function(){var element=document.getElementsByClassName('viewArea')[0];
            element.innerHTML="";render(Application.messageList);});
        saveMessages(Application.messageList);
        saveName(Application.name);
	}

}
function reassociateMessages(name,done){
    for(var i=0;i<Application.messageList.length;i++)
    {
      // var tempName=Application.messageList[i].author;
        //if(name!=tempName)
            changeClass(Application.messageList[i]);

        var messageToChange= {
            id:Application.messageList[i].id,
            text:Application.messageList[i].text,
            type:"othersMessage"
        };
        if(Application.messageList[i].type.indexOf("Changed")>-1) {
            messageToChange.type="messageChanged";

        }
        if(Application.messageList[i].type.indexOf("Deleted")>-1) {
            messageToChange.type="messageDeleted";
        }
        ajax('PUT', Application.mainUrl, JSON.stringify(messageToChange), function(){
             done();
        });
       /* else
            setOwnership(Application.messageList[i]);*/
    }
}
function changeClass(message)
 {
     if(message.type=='yourMessage')
         message.type='othersMessage';
     if(message.type=='yourMessageChanged')
         message.type='messageChanged';
     if(message.type=='yourMessageDeleted')
         message.type='messageDeleted';
 }
 function setOwnership(message)
 {
     if(message.type=='othersMessage')
         message.type='yourMessage';
     if(message.type=='messageChanged')
         message.type='yourMessageChanged';
     if(message.type=='messageDeleted')
         message.type='yourMessageDeleted';
 }
function onAddButtonClick(evtObj,done){
	var messageText = document.getElementById('messageText');
    	var message = newMessage(messageText.value);

    	if(messageText.value == '')
    		return;

    	//Application.messageList.push(message);
    	//saveMessages(Application.messageList);
    	messageText.value = '';


        var element=document.getElementsByClassName('viewArea')[0];
        element.innerHTML="";
        ajax('POST', Application.mainUrl, JSON.stringify(message), function(){
         Application.messageList.push(message);
            done();
        });



} 
function onStartRemoving()
{
    alert("Click on the message you want to remove");
    Application.isDeleting=true;
}
function onStartEditing()
{
    alert("Click on the message you want to edit");
    Application.isEditing=true;
}
function onRemoveMessage(messageToRemove)
{
    var result=confirm("Do you really want to remove this message?");
        if(result==false){
            Application.isDeleting=false;
        }
        else{
         removeMessage(messageToRemove,function(){render(Application.messageList);});
         Application.isDeleting=false;
        }
}
function onEditMessage(messageToEdit)
{
    var result=confirm("Do you really want to edit this message?");
        if(result==false){
            Application.isEditing=false;
        }
        else{
         editMessage(messageToEdit,function(){render(Application.messageList);});
         Application.isEditing=false;
        }
}
function indexByElement(element, messages){
	var id = element.attributes['message-id'].value;

	return messages.findIndex(function(item) {
		return item.id == id;
	});
}

function removeMessage(messageToRemove,done){
    var index = indexByElement(messageToRemove, Application.messageList);
    var message = Application.messageList[index];
    message.type='yourMessageDeleted';
    message.text='Message was deleted';
    var element=document.getElementsByClassName('viewArea')[0];
    element.innerHTML="";

    var messageToDelete = {
        id:message.id
    };

    ajax('DELETE', Application.mainUrl, JSON.stringify(messageToDelete), function(){
       // Application.taskList.splice(index, 1);
        done();
    });
    //  saveMessages(Application.messageList);
    render(Application.messageList);

}
function editMessage(messageToEdit,done){
    var newText=prompt("Input new message here");
    if(newText){
       var index = indexByElement(messageToEdit, Application.messageList);
       var message = Application.messageList[index];
       message.type='yourMessageChanged';
       message.text=newText;
       var element=document.getElementsByClassName('viewArea')[0];
       element.innerHTML="";

        var messageToChange= {
            id:message.id,
            text:newText,
        };
        ajax('PUT', Application.mainUrl, JSON.stringify(messageToChange), function(){
            done();
        });
       saveMessages(Application.messageList);
      // render(Application.messageList);
    }
}

function ajax(method, url, data, continueWith, continueWithError) {
    var xhr = new XMLHttpRequest();

    continueWithError = continueWithError || defaultErrorHandler;
    xhr.open(method || 'GET', url, true);

    xhr.onload = function () {
        if (xhr.readyState !== 4)
            return;

        if(xhr.status != 200) {
            continueWithError('Error on the server side, response ' + xhr.status);
            return;
        }

        if(isError(xhr.responseText)) {
            continueWithError('Error on the server side, response ' + xhr.responseText);
            return;
        }
        document.getElementsByClassName("serverError")[0].style.display = "none";
        continueWith(xhr.responseText);
    };

    xhr.ontimeout = function () {
        continueWithError('Server timed out !');
    };

    xhr.onerror = function (e) {
        var errMsg = 'Server connection error !\n'+
            '\n' +
            'Check if \n'+
            '- server is active\n'+
            '- server sends header "Access-Control-Allow-Origin:*"\n'+
            '- server sends header "Access-Control-Allow-Methods: PUT, DELETE, POST, GET, OPTIONS"\n';
        document.getElementsByClassName("serverError")[0].style.display = "block";
        continueWithError(errMsg);
    };

    xhr.send(data);
}


function uniqueId() {
    var date = Date.now();
    var random = Math.random() * Math.random();

    return Math.floor(date * random);
}

function output(value){
    var output = document.getElementById('output');

    output.innerText = JSON.stringify(value, null, 2);
}

function defaultErrorHandler(message) {
    console.error(message);
    output(message);
}

function isError(text) {
    if(text == "")
        return false;

    try {
        var obj = JSON.parse(text);
    } catch(ex) {
        return true;
    }

    return !!obj.error;
}

function formDate()
{
    var today = new Date();
    return today;
}
function uniqueId() {
	var date = Date.now();
	var random = Math.random() * Math.random();

	return Math.floor(date * random);
}