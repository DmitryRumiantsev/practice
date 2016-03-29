var name='Anonymous';
var isDeleting=false;
var isEditing=false;
var messageList=[];

function run(){
	var appContainer = document.getElementsByClassName('appContainer')[0];

	appContainer.addEventListener('click', delegateEvent);
	appContainer.addEventListener('change', delegateEvent);
	 name=loadName()|| 'Anonymous';
    messageList =loadMessages() ||  [
    			newMessage('Сделать разметку'),
    			newMessage('Выучить JavaScript'),
    			newMessage('Написать чат !')
    		];
	render(messageList);
}

function render(messages) {
	for(var i = 0; i < messages.length; i++) {
		renderMessage(messages[i]);
	}
}
function loadMessages() {
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	var item = localStorage.getItem("MessageList");

	return item && JSON.parse(item);
}
function loadName() {
	if(typeof(Storage) == "undefined") {
		alert('localStorage is not accessible');
		return;
	}

	var userName= localStorage.getItem("Username");

	return userName && JSON.parse(userName);
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

	localStorage.setItem("MessageList", JSON.stringify(listToSave));
}
function renderMessageType(element, message){
	element.classList.add(message.type);
	element.firstElementChild.childNodes[0].appendChild(document.createTextNode(message.senderName));
	element.firstElementChild.childNodes[1].appendChild(document.createTextNode(message.time));
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
	    type:'yourMessage',
	    senderName: ''+name,
	    time: ''+ formDate().toGMTString(),
		text:text,
		id: '' + uniqueId()
	};
}
function delegateEvent(evtObj) {
	if(evtObj.type === 'click'){
	if((evtObj.target.classList.contains('sendMessage')||evtObj.target.classList.contains('send'))){
    		onAddButtonClick(evtObj);
    	}
	if(evtObj.target.classList.contains('changeName')){
	    onChangeUserName(evtObj);
	}
	if((evtObj.target.classList.contains('removeMessage')||evtObj.target.parentElement.classList.contains('removeMessage'))){
    		onStartRemoving(evtObj);
    	}
    if((evtObj.target.classList.contains('editMessage')||evtObj.target.parentElement.classList.contains('editMessage'))){
        		onStartEditing(evtObj);
        	}

    if(isDeleting){
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
     if(isEditing){
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
function onChangeUserName(){
	var result=prompt("Input username",name);
	if(result){
        name=result;
        reassociateMessages(name);
        saveMessages(messageList);
        saveName(name);
	}
	var element=document.getElementsByClassName('viewArea')[0];
	element.innerHTML="";
    render(messageList);
}
function reassociateMessages(name){
    for(var i=0;i<messageList.length;i++)
    {
       var tempName=messageList[i].senderName;
        if(name!=tempName)
            changeClass(messageList[i]);
        else
            setOwnership(messageList[i]);
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
function onAddButtonClick(){
	var messageText = document.getElementById('messageText');
    	var message = newMessage(messageText.value);

    	if(messageText.value == '')
    		return;

    	messageList.push(message);
    	saveMessages(messageList);
    	messageText.value = '';
    	render([message]);

} 
function onStartRemoving()
{
    alert("Click on the message you want to remove");
    isDeleting=true;
}
function onStartEditing()
{
    alert("Click on the message you want to edit");
    isEditing=true;
}
function onRemoveMessage(messageToRemove)
{
    var result=confirm("Do you really want to remove this message?");
        if(result==false){
            isDeleting=false;
        }
        else{
         removeMessage(messageToRemove);
         isDeleting=false;
        }
}
function onEditMessage(messageToEdit)
{
    var result=confirm("Do you really want to edit this message?");
        if(result==false){
            isEditing=false;
        }
        else{
         editMessage(messageToEdit);
         isEditing=false;
        }
}
function indexByElement(element, messages){
	var id = element.attributes['message-id'].value;

	return messages.findIndex(function(item) {
		return item.id == id;
	});
}

function removeMessage(messageToRemove){
    var index = indexByElement(messageToRemove, messageList);
    var message = messageList[index];
    message.type='yourMessageDeleted';
    message.text='Message was deleted';
    var element=document.getElementsByClassName('viewArea')[0];
    element.innerHTML="";
    saveMessages(messageList);
    render(messageList);

}
function editMessage(messageToEdit){
    var newText=prompt("Input new message here");
    if(newText){
       var index = indexByElement(messageToEdit, messageList);
       var message = messageList[index];
       message.type='yourMessageChanged';
       message.text=newText;
       var element=document.getElementsByClassName('viewArea')[0];
       element.innerHTML="";
       saveMessages(messageList);
       render(messageList);
    }
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