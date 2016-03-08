var name='Anonymous';
function run(){
	var appContainer = document.getElementsByClassName('appContainer')[0];

	appContainer.addEventListener('click', delegateEvent);
	appContainer.addEventListener('change', delegateEvent);

	//updateCounter();
}

function delegateEvent(evtObj) {
	if(evtObj.type === 'click' && (evtObj.target.classList.contains('sendMessage')||evtObj.target.classList.contains('send'))){
		onAddButtonClick(evtObj);
	}
	if(evtObj.type === 'change' && evtObj.target.nodeName == 'INPUT'){
		var labelEl = evtObj.target.parentElement;

		onToggleItem(labelEl);
	}
	if(evtObj.type === 'click' && evtObj.target.classList.contains('changeName')){
    		onChangeUserName(evtObj);
    	}
}

function onChangeUserName(){
	var result=prompt("Input username",name);
	if(result)
	    name=result;
}
function onAddButtonClick(){
	var messageText = document.getElementById('messageText');
	addMessage(messageText.value);
	messageText.value = '';
	//updateCounter();
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