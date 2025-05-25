window.addEventListener("DOMContentLoaded", () => {
    try {
        const selectedContactId = '[[${SelectedContactId}]]';
        const selectedContactName = '[[${SelectedContactName}]]';

        if (!selectedContactId || !selectedContactName) {
            setDefaultState();
        } else {
            initializeChat(selectedContactId, selectedContactName);
        }
    } catch (error) {
        console.error("An error occurred: ", error);
    }
});

function setDefaultState() {
    document.getElementById('activeContactName').textContent = 'Please select a contact to start chatting';
    document.getElementById('messageForm').querySelector('button').disabled = true;
    document.querySelector('.contacts-list').style.display = 'block';
}

function initializeChat(contactId, contactName) {
    document.getElementById('activeContactName').textContent = contactName;
    document.getElementById('receiverId').value = contactId;
    loadMessages(contactId);
    document.querySelector('.contacts-list').style.display = 'none';
    enableChatElements();
}

function selectConversation(element) {
    const activeContactName = element.querySelector("span:nth-child(2)").textContent;
    document.getElementById('activeContactName').textContent = activeContactName;

    const contactId = element.getAttribute('data-id');
    document.getElementById('receiverId').value = contactId;

    loadMessages(contactId);
    enableChatElements();
}

function enableChatElements() {
    document.getElementById('messageForm').querySelector('button').disabled = false; // Enable Send button
    document.getElementById('messageContent').disabled = false; // Enable textarea
}

function loadMessages(userId) {
    const url = `/messages/user/${userId}`;

    fetch(url)
        .then(response => handleFetchResponse(response))
        .then(data => displayMessages(data))
        .catch(error => handleErrorLoadingMessages(error));
}

function handleFetchResponse(response) {
    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return response.json();
}

function displayMessages(data) {
    const messageList = document.getElementById('messageList');
    messageList.innerHTML = '';

    if (data && data.length > 0) {
        data.forEach(message => appendMessage(message, messageList));
    } else {
        messageList.innerHTML = '<p>No messages yet. Start a conversation!</p>';
    }
}

function appendMessage(message, messageList) {
    const messageElement = document.createElement('div');
    messageElement.classList.add(
        message.senderId === document.getElementById('senderId').value
            ? 'message-sent'
            : 'message-received'
    );
    messageElement.innerHTML = `
        <p>${message.content}</p>
        <span class="timestamp">${new Date(message.timestamp).toLocaleTimeString()}</span>
    `;
    messageList.appendChild(messageElement);
}

function handleErrorLoadingMessages(error) {
    console.error("Error loading messages:", error);
    const messageList = document.getElementById('messageList');
    messageList.innerHTML = `<p class="error-message">Unable to load messages. Please try again later.</p>`;
}

document.getElementById('messageForm').addEventListener('submit', function (event) {
    event.preventDefault();

    const messageContent = document.getElementById('messageContent').value;
    const senderId = document.getElementById('senderId').value;
    const receiverId = document.getElementById('receiverId').value;

    sendMessage(messageContent, senderId, receiverId);
});

function sendMessage(content, senderId, receiverId) {
    const messageData = { content, senderId, receiverId };

    fetch('/messages/send', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(messageData)
    })
        .then(response => handleSendMessageResponse(response))
        .catch(error => handleErrorSendingMessage(error));
}

function handleSendMessageResponse(response) {
    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return response.json();
}

function handleErrorSendingMessage(error) {
    console.error("Error sending message:", error);
    alert("There was an error sending the message. Please try again.");
}

function displaySentMessage(data) {
    document.getElementById('messageContent').value = '';

    const messageList = document.getElementById('messageList');
    const messageElement = document.createElement('div');
    messageElement.classList.add('message-sent');
    messageElement.innerHTML = `
        <p>${data.content}</p>
        <span class="timestamp">${new Date().toLocaleTimeString('en-US')}</span>
    `;
    messageList.appendChild(messageElement);

    messageList.scrollTop = messageList.scrollHeight;
}
