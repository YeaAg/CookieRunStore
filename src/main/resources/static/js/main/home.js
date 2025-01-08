const chatbotSection = document.getElementById('chatbot-section');
const messageContainer = chatbotSection.querySelector('.message-container');
const chatbotInput = chatbotSection.querySelector('.input-container input');

// 질문 작성창에 작성후 엔터 입력했을 때
chatbotInput.onkeydown = event => {
    const sentence = chatbotInput.value.trim();
    if(event.key.toUpperCase() === 'ENTER' && sentence !== ''){
        chatbotInput.value = ''; // input에 작성한 질문은 없애줌
        create_user_message(sentence); // 사용자 질문 나타내기
        request_question(sentence); // 챗봇 응답 받기
    }
}

// 질문을 입력했을 때 유저 메세지 나타내기
function create_user_message(sentence){
    messageContainer.insertAdjacentHTML(`beforeend`,
        `<div class="user message">
            <div class="text">${sentence}</div>
        </div>`
    );
}

// 챗봇의 응답 메세지 생성하기
function create_chatbot_message(answer){
    messageContainer.insertAdjacentHTML(`beforeend`,
        `<div class="bot message">
            <div class="image-container">
                <i class="bi bi-robot"></i>
            </div>
            <div class="text">${answer}</div>
        </div>`
    );
}

// 질문 요청 전송
function request_question(sentence){
    const csrfToken = document.querySelector('meta[name=_csrf]').getAttribute('content');
    fetch(`/chatbot`, {
        method: "POST",
        headers: {
            "X-CSRF-TOKEN": csrfToken,
            "Content-Type": "application/json"
        },
        body: sentence
    }).then(response => {
        // 챗봇 에러 발생 시
        if(!response.ok){
            alert('시스템 에러 발생!');
            throw new Error();
        }
        return response.text();
    }).then(create_chatbot_message);
}