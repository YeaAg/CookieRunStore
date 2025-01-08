const productForm = document.forms.namedItem('product');

const mainImage = document.querySelector('.main-image-container > img');
const subImageButtons = document.querySelectorAll('.sub-image-container > button');
const mainImageSrc = mainImage.src;

const productOptionSelect = document.getElementById('product-option-select');
const productOptionUl = document.getElementById('product-option-ul');

const buyButton = document.querySelector('.buy-btn');
const cartButton = document.querySelector('.cart-btn');
const heartButton = document.querySelector('.heart-btn');

const reviewForm = document.getElementById('review-form')

/// 상품 이미지 변경하기
subImageButtons.forEach(subImageButton => {
    subImageButton.onmouseenter = () => {
        const imageTag = subImageButton.querySelector('img');
        mainImage.src = imageTag.src;
    }
    subImageButton.onmouseleave = () => {
        mainImage.src = mainImageSrc;
    }
});

/**************************************/

/// 옵션 추가하기 (옵션이 존재할 때)
if(productOptionSelect != null){
    productOptionSelect.onchange = () => {
        // 현재 추가된 옵션들
        const productOptions = productOptionUl.getElementsByTagName('li');
        // 방금 막 추가를 한 옵션을 찾는다
        for(const option of productOptionSelect.options){
            // 사용자가 추가하려고 선택한 옵션이라면
            if(option.selected){
                const optionValue = option.value;
                // 이미 추가되어있는 옵션 리스트 중에서, 현재 선택한 항목과 같은 것이 있는지 확인
                const existsOption = [...productOptions].find(productOption => productOption.id === optionValue)
                // 이미 추가가 되어있다면
                if(existsOption !== undefined){
                    // 수량만 1 올린다
                    const amountInput = existsOption.querySelector('input');
                    amountInput.value = +amountInput.value + 1;
                    return; // 새로 추가는 하지 않는다
                }

                const optionText = option.textContent;
                const plusStr = optionText.lastIndexOf('+')
                const title = optionText.substring(0, plusStr-1);
                const price = optionText.slice(plusStr+1, -2);

                productOptionUl.insertAdjacentHTML(`beforeend`,
                    `<li id="${optionValue}">
                        <b>${title}</b>
                        <label>
                            <button type="button" class="amount-minus-btn" onclick="change_option_amount(this)"><i class="bi bi-dash-lg"></i></button>
                            <input type="number" value="1">
                            <button type="button" class="amount-plus-btn" onclick="change_option_amount(this)"><i class="bi bi-plus-lg"></i></button>
                        </label>
                        <button type="button" onclick="remove_option(this)"><i class="bi bi-x-square-fill"></i></button>
                        <span>${price}원</span>
                    </li>`
                )
            }
        }

    }
}

/// 옵션 삭제하기
function remove_option(removeBtn){
    if(confirm('정말 삭제하시겠습니까?')){
        removeBtn.parentElement.remove();
    }
}
/// 옵션 수량 변경하기
function change_option_amount(amountButton){
    const numberInput = amountButton.parentElement.querySelector('input');
    if(amountButton.className.includes('minus')){
        numberInput.value = +numberInput.value - 1;
    }else{
        numberInput.value = +numberInput.value + 1;
    }
}

/**************************************/

////// 상품을 즉시 주문하기
buyButton.onclick = (e) => {
    e.preventDefault();
    const cartObject = create_cart_object();
    // 주문을 준비하는 곳에 요청
    request('/order/prepare', [cartObject]).then(() => {
        location.href = '/user/order'; // 주문 페이지로 이동
    });
}

////// 상품을 카트에 추가하기
cartButton.onclick = () => {
    const cartObject = create_cart_object();
    request('/cart', cartObject.product).then(() => {
        // 장바구니 담기일 경우
        if(confirm('장바구니에 상품이 담겼습니다. 장바구니로 이동하시겠습니까?')){
            location.href = '/user/cart';
        }
    });
}

function create_cart_object(){
    // 추가된 옵션이 있다면 추가된 옵션들의 id를 배열로 수집한다
    const productOptions = productOptionUl.getElementsByTagName('li');
    const options = [...productOptions].map(productOption => {
        const no = productOption.id;
        const amount = +productOption.querySelector('input').value;
        return {
            no: no,
            amount: amount
        };
    });

    return {
        // cartDTO 형태
        product: {
            no: productForm.id,
            options: options
        },
        amount: 1
    }
}

// 주문/장바구니에 상품 추가하는 요청
function request(url, requestBody){
    const csrfToken = document.querySelector('meta[name=_csrf]').getAttribute('content');
    // 장바구니 담기 POST 요청 전송
     return fetch(url, {
        method: "POST",
        headers: {
            "X-CSRF-TOKEN": csrfToken,
            "Content-Type": "application/json"
        },
        body: JSON.stringify(requestBody)
    }).then(response => {
        // 로그인이 안된 유저가 클릭 시
        if(response.status === 401){
            alert('로그인을 먼저 해주세요');
            throw new Error();
        }
        else if(!response.ok){
            alert('시스템 에러 발생!');
            throw new Error();
        }
    });
}

/**************************************/

// 화면 로딩 시 최초 리뷰 로딩
const productNo = productForm.id;
load_review(null, `/product/${productNo}/review`);
/// 상품에 대한 리뷰 불러오기
function load_review(event, url){
    if(event !== null){
        event.preventDefault();
    }
    reviewForm.innerHTML = '';
    fetch(url)
        .then(response => response.text())
        .then(reviewTemplate => {
            reviewForm.insertAdjacentHTML(`beforeend`, reviewTemplate)
        });
}
