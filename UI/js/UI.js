var name='Anonymous';
var isDeleting=false;
var isEditing=false;
function run(){
	var appContainer = document.getElementsByClassName('appContainer')[0];

	appContainer.addEventListener('click', delegateEvent);
	appContainer.addEventListener('change', delegateEvent);

	//updateCounter();
}

function delegateEvent(evtObj) {

	if(evtObj.type === 'change' && evtObj.target.nodeName == 'INPUT'){
		var labelEl = evtObj.target.parentElement;
		onToggleItem(labelEl);
	}
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
	}

}
function reassociateMessages(name){
    var messages = document.getElementsByClassName('viewArea')[0];
    for(var i=0;i<messages.childNodes.length;i++)
    {
    if(!messages.childNodes[i].classList.contains('space')){
       var tempName=messages.childNodes[i].childNodes[0].childNodes[0].textContent;
        if(name!=tempName)
            changeClass(messages.childNodes[i]);
        else if(!messages.childNodes[i].classList.contains('space') && name==tempName)
            setOwnership(messages.childNodes[i]);
            }
    }
}
function changeClass(element)
 {
     if(element.classList.contains('yourMessage'))
         element.className='othersMessage';
     if(element.classList.contains('yourMessageChanged'))
         element.className='messageChanged';
     if(element.classList.contains('yourMessageDeleted'))
         element.className='messageDeleted';
 }
 function setOwnership(element)
 {
     if(element.classList.contains('othersMessage'))
         element.className='yourMessage';
     if(element.classList.contains('messageChanged'))
         element.className='yourMessageChanged';
     if(element.classList.contains('messageDeleted'))
         element.className='yourMessageDeleted';
 }
function onAddButtonClick(){
	var messageText = document.getElementById('messageText');
	addMessage(messageText.value);
	messageText.value = '';
	//updateCounter();
} 
function onStartRemoving()
{
    alert("Click on the message you want to remove");
    isDeleting=true;
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
function removeMessage(messageToRemove){
    messageToRemove.className='yourMessageDeleted';
    messageToRemove.childNodes[0].childNodes[3].data='Message was deleted';

}
function onToggleItem(labelEl) {
	if(labelEl.classList.contains('strikeout')) {
		labelEl.classList.remove('strikeout');
	}
	else {
		labelEl.classList.add('strikeout');
	}
	updateCounter();
}

function addMessage(value) {
	if(!value){
		return;
	}
	var message= createMessage(value);
	var messages = document.getElementsByClassName('viewArea')[0];
	messages.appendChild(message);
	//updateCounter();
}

function createMessage(text){
	var divItem = document.createElement('div');
	var paragraph = document.createElement('p');
	divItem.classList.add('yourMessage');
	formParagraph(paragraph,text);
	divItem.appendChild(paragraph);
	return divItem;
}
function formParagraph(paragraph,text)
{
    var header= document.createElement('div');
    header.appendChild(document.createTextNode(name));
    paragraph.appendChild(header);
    appendDate(paragraph);
    paragraph.appendChild(document.createTextNode(text));
}
function formDate()
{
    var today = new Date();
    return today;
}
function appendDate(paragraph)
{
    var date= document.createElement('div');
    var breakItem = document.createElement('br');
    date.appendChild(document.createTextNode(formDate().toGMTString()));
    paragraph.appendChild(date);
     paragraph.appendChild(breakItem);
}
function updateCounter(){
	var items = document.getElementsByClassName('items')[0];
	var counter = document.getElementsByClassName('counter-holder')[0];

    counter.innerText = items.children.length.toString();
}