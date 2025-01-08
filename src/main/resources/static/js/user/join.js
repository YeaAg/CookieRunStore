const idInput = document.getElementById('id');
const pwInput = document.getElementById('password');
const pwReInput = document.getElementById('password-re');
const idCheckBtn = document.querySelector('.id-input-container button')

const idCheckInput = document.querySelector('input[name="id-check"]');
const idCheckValidInput = document.querySelector('input[name="id-check-valid"]');

const telHead = document.querySelector('.tel-input-container select');
const [telMiddle, telTail] = document.querySelectorAll('.tel-input-container input');
const telAuthBtn = document.querySelector('.tel-input-container button');
const [emailHead, emailTail] = document.querySelectorAll('.email-input-container input');
const emailSelect = document.querySelector('.email-input-container select');
const emailAuthBtn = document.querySelector('.email-input-container button');
const emailAuthConfirmInput = document.querySelector('.email-auth-input-container input');
const emailAuthConfirmBtn = document.querySelector('.email-auth-input-container button');
const [joinBtn, cancelBtn] = document.querySelectorAll('.join-btn-section > button');
const [idInputError, pwInputError, telInputError, emailInputError] = document.querySelectorAll('.error-container');

let telAuthCompleted = false; // 휴대폰 인증 여부
let emailAuthCompleted = false; // 이메일 인증 여부
IMP.init("imp38408183"); // Portone 모듈 초기화
/************************************************************************/
// id 중복 체크 버튼 클릭 시
idCheckBtn.onclick = () => {
    const id = idInput.value;
    if(id.trim() === ''){
        alert('ID를 입력해주세요!');
        return;
    }
    fetch(`/user/id/${id}`)
        .then(response => {
            idCheckInput.value = id;
            idCheckValidInput.value = false;

            switch (response.status){
                case 200:
                    alert('해당 아이디는 사용 가능합니다^-^');
                    idCheckValidInput.value = true;
                    break;
                case 302:
                    alert('해당 아이디는 사용 불가능합니다ㅠㅠ');
                    break;
            }
        })
}

///// ********** 휴대폰
// 휴대폰 인증 버튼 클릭 시
telAuthBtn.onclick = () => {
    // IMP.certification(param, callback) 호출
    IMP.certification(
        {
            // param
            channelKey: "channel-key-08856ae7-c4ed-48a8-95d1-c9f7ab98014a",
            merchant_uid: "ORD20180131-0000011", // 주문 번호
            // m_redirect_url: "{리디렉션 될 URL}", // 모바일환경에서 popup:false(기본값) 인 경우 필수
            // popup: false, // PC환경에서는 popup 파라미터가 무시되고 항상 true 로 적용됨
        },
        function (importResponse) {
            if (importResponse.success) {
                const csrfToken = document.forms[0].querySelector('input[name=_csrf]').value;
                const impUid = importResponse['imp_uid'];
                fetch(`/tel/auth`, {
                    method: "POST",
                    headers: {
                        "X-CSRF-TOKEN": csrfToken
                    },
                    body: impUid
                }).then(response => {
                    if(response.ok){
                        alert('본인인증이 완료되었습니다');
                    }
                })
                telAuthCompleted = true;
            } else {
                alert('본인인증에 실패하였습니다');
                telAuthCompleted = false;
            }
        },
    );
}

///// ********** 이메일
/// 이메일 직접입력/자동 설정
emailSelect.onchange = () => {
    if(emailSelect.value === '직접입력'){
        emailTail.value = '';
        emailTail.readOnly = false;
    }else{
        emailTail.value = emailSelect.value;
        emailTail.readOnly = true;
    }
}
/// 이메일 인증 버튼 클릭 시
emailAuthBtn.onclick = () => {
    // CSRF 토큰 값을 가져온다
    const csrfToken = document.forms[0].querySelector('input[name=_csrf]').value;
    const email = `${emailHead.value}@${emailTail.value}`;
    emailAuthCompleted = false;
    fetch(`/email/auth`, {
        method: "POST",
        headers: { 'X-CSRF-TOKEN' : csrfToken },
        body: email
    }).then(response => {
        if (response.ok && response.status === 200){
            alert("인증번호를 발송했습니다!")
        }else{
            alert("인증번호 전송에 실패했습니다")
        }
    });
}
/// 이메일 인증 확인 버튼 클릭 시
emailAuthConfirmBtn.onclick = () => {
    // 사용자가 입력한 인증번호를 가져옴
    const certNumber = emailAuthConfirmInput.value;
    const email = `${emailHead.value}@${emailTail.value}`;

    fetch(`/email/auth?email=${email}&certNumber=${certNumber}`)
        .then(response => {
            if (response.ok && response.status === 200){
                alert('인증 성공!');
                emailAuthCompleted = true;
                // 인증번호 입력과 버튼 클릭 못하게 막음
                emailAuthConfirmInput.setAttribute('disabled','');
                emailAuthConfirmBtn.setAttribute('disabled','');
            }else{
                emailAuthCompleted = false;
                alert('인증번호가 다릅니다');
            }
        })
}

///// ********** 입력 검사
function check_input_values(){
    idInputError.removeAttribute('active');
    pwInputError.removeAttribute('active');
    telInputError.removeAttribute('active');
    emailInputError.removeAttribute('active');

    //// 아이디 체크
    const id = idInput.value.trim();
    if(!((/^[a-z][0-9a-zA-Z]*$/.test(id)) && (id.length >= 4) && (id.length <= 15))){
        idInputError.setAttribute('active','');
        return false;
    }

    //// 비밀번호 체크
    const pw = pwInput.value.trim();
    const pwRe = pwReInput.value.trim();
    const pwErrorSpan = pwInputError.querySelector('span');
    // 패스워드 잘못 적음
    if(!/^[0-9a-zA-Z~!@#$%^&*()_=+.-]{4,10}$/.test(pw)){
        pwInputError.setAttribute('active', '');
        pwErrorSpan.textContent = '비밀번호 형식이 잘못되었습니다';
        return false;
    }
    // 패스워드 재입력과 다름
    if(pw !== pwRe){
        pwInputError.setAttribute('active', '');
        pwErrorSpan.textContent = '기존 입력한 비밀번호와 재입력한 비밀번호가 다릅니다';
        return false;
    }
    // 전화번호 검사
    const telHeadValue = telHead.value.trim();
    const telMiddleValue = telMiddle.value.trim();
    const telTailValue = telTail.value.trim();
    const tel = `${telHeadValue}-${telMiddleValue}-${telTailValue}`;
    if(!/^(010|011|017|019|018)-[0-9]{3,4}-[0-9]{4}$/.test(tel)){
        telInputError.setAttribute('active', '');
        return false;
    }
    // 이메일 검사
    const emailHeadValue = emailHead.value.trim();
    const emailTailValue = emailTail.value.trim();
    const email = `${emailHeadValue}@${emailTailValue}`;
    if(!/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(email)){
        emailInputError.setAttribute('active', '');
        return false;
    }

    return true;
}

///// ********** 회원가입 버튼 클릭 시
// 1) submit 버튼에서 event의 기본 동작 취소하기 v
// 2) form의 submit event의 기본 동작 취소하기
joinBtn.onclick = (event) => {
    // input 값들 중 하나라도 잘못 적었다면
    // if(!check_input_values()){
    //     event.preventDefault();
    //     return;
    // }
    const id = idCheckInput.value;
    const valid = idCheckValidInput.value;
    // 현재 회원가입하려고 하는 아이디가, 중복체크해서 사용한 아이디와 다르며
    // 중복체크를 통과하지 못했다면 회원가입을 시키면 안된다
    // if(idInput.value !== id || valid === 'false'){
    //     alert('ID 중복 체크는 필수입니다');
    //     event.preventDefault();
    //     return;
    // }
    // if(!telAuthCompleted){
    //     alert('휴대폰 인증은 필수입니다');
    //     event.preventDefault();
    //     return;
    // }
    // if (!emailAuthCompleted){
    //     alert('이메일 인증은 필수입니다');
    //     event.preventDefault();
    //     return;
    // }
}