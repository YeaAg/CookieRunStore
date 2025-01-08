const carts = document.querySelectorAll('tbody tr');
const selectDeleteBtn = document.getElementById('select-delete-btn');
const orderSelectButton = document.getElementById('order-select-button');
const orderAllButton = document.getElementById('order-all-button');

// 선택 항목을 삭제하기 버튼
selectDeleteBtn.onclick = () => {
    // 선택 항목들을 가져오기
    carts.forEach(cart => {
        const checkBox = cart.querySelector('input[type=checkbox]');
        if(checkBox.checked){
            const cartId = cart.id;
            remove_cart(cartId);
        }
    })
}

// 선택 항목을 주문하기 버튼
orderSelectButton.onclick = () => {
    const cartObject = create_cart_object(true);
    // 주문을 준비하는 곳에 요청
    request('/order/prepare', cartObject).then(() => {
        location.href = '/user/order'; // 주문 페이지로 이동
    });
}

// 모든 항목을 주문하기 버튼
// 1)
orderAllButton.onclick = () => {
    const cartObject = create_cart_object(false);
    // 주문을 준비하는 곳에 요청
    request('/order/prepare', cartObject).then(() => {
        location.href = '/user/order'; // 주문 페이지로 이동
    });
}
// 2) formData를 사용하기 (js에 fetch요청 보낼 때 많이 함)
// orderAllButton.onclick = () => {
//     const csrfToken = document.querySelector('meta[name=_csrf]').getAttribute('content');
//     const cartNumbers = [...carts].map(cart => cart.id);
//     const formData = new FormData();
//     formData.append('_csrf', csrfToken);
//     formData.append('cartNumbers', cartNumbers)
//     fetch('/user/order', {
//         method: "POST",
//         body: formData
//     }).then(r => r.text()).then(console.log);
// }

// 주문하려는 상품 객체 생성
function create_cart_object(isSelected){
    const objectList = [];
    [...carts].forEach(cart => {
        const checkBox = cart.querySelector('input[type=checkbox]');
        if(isSelected && !checkBox.checked){
            return;
        }
        const cartNo = +cart.id;
        const amount = +cart.querySelector('.product-amount-container input').value;
        const productNo = +checkBox.getAttribute('data');
        // 추가된 옵션이 있다면 추가된 옵션들의 id를 배열로 수집한다
        const productOptions = cart.querySelectorAll('.option-list > li');
        const options = [...productOptions].map(productOption => {
            const [no, amount] = productOption.getAttribute('data').split('-');
            return {
                no: +no,
                amount: +amount
            };
        });
        // cartDTO 형태
        objectList.push({
            no: cartNo,
            product:{
                no: productNo,
                options: options
            },
            amount: amount
        });
    });
    return objectList;
}

// 주문하기
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
        }else if(!response.ok){
            alert('시스템 에러 발생!');
            throw new Error();
        }
    });
}
// 주문하기
// function order(isSelected){
//     const csrfToken = document.querySelector('meta[name=_csrf]').getAttribute('content');
//
    // const form = document.createElement('form');
    // const csrfInput = document.createElement('input');
    // csrfInput.name = '_csrf';
    // csrfInput.value = csrfToken;
    // form.appendChild(csrfInput);
    // document.body.appendChild(form);
    // [...carts].forEach(cart => {
    //     const checkBox = cart.querySelector('input[type=checkbox]');
    //     if(isSelected && !checkBox.checked){
    //         return;
    //     }
    //     const input = document.createElement('input');
    //     input.name = 'cartNumbers';
    //     input.value = cart.id;
    //     form.appendChild(input);
    // });
    // form.method = 'POST';
    // form.action = '/user/order';
    // form.submit();
// }

/**********************************************/

carts.forEach(cart => {
    const cartId = cart.id;
    const amountInput = cart.querySelector('.amount-change-label > input');
    amountInput.onchange = () => {
        change_cart_amount(cartId, +amountInput.value);
    }
    cart.querySelector('.amount-plus-btn').onclick = () => {
        change_cart_amount(cartId, +amountInput.value + 1);
    };
    cart.querySelector('.amount-minus-btn').onclick = () => {
        change_cart_amount(cartId, +amountInput.value - 1);
    };
    cart.querySelector('.delete-btn').onclick = () => {
        remove_cart(cartId);
    }
})

function change_cart_amount(cartId, amount){
    const csrfToken = document.querySelector('meta[name=_csrf]').getAttribute('content');
    fetch(`/cart/${cartId}`, {
        method: "PATCH",
        headers: {
            "X-CSRF-TOKEN": csrfToken,
            "Content-Type": "application/json"
        },
        body: amount
    }).then(response => {
        if (response.ok){
            location.reload();
        }
    })
}

// 잘못하면 패치를 다 못했는데 location.reload()가 될 수도 있기 때문에 fetch부분을 Promise.all을 사용해서 수장하면 좋다
function remove_cart(cartId){
    const csrfToken = document.querySelector('meta[name=_csrf]').getAttribute('content');
    fetch(`/cart/${cartId}`, {
        method: "DELETE",
        headers: {"X-CSRF-TOKEN": csrfToken}
    }).then(response => {
        if (response.ok){
            location.reload();
        }
    })
}
