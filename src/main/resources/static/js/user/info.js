const orderLies = document.querySelectorAll('.order-list-section li');
const orderDetailContainer = document.querySelector('.order-detail-container');
const orderDetailTbody = orderDetailContainer.querySelector('tbody');
const orderDetailCloseBtn = document.getElementById('order-detail-close-btn');

const reviewContainer = document.querySelector('.review-container');
const reviewForm = reviewContainer.querySelector('form');
const reviewTextArea = reviewContainer.querySelector('textarea');
const reviewStars = reviewContainer.querySelectorAll('.review-user-stars button > i');
const [reviewWriteBtn, reviewCloseBtn] = reviewForm.querySelectorAll('.button-container button');
const reviewProductsSelect = document.getElementById('review-products');

// 모든 주문 상품에 대해서 적용
orderLies.forEach(orderLi => {
    const orderDetailButton = orderLi.querySelector('.order-detail-btn');
    const orderReviewButton = orderLi.querySelector('.order-review-btn');
    const orderATag = orderLi.querySelector('.order-number a');
    const orderId = orderATag.textContent;
    orderATag.onclick = event => {
        event.preventDefault();
        clicked_order_item(orderId).then(order => {
            create_order_details(order);
            orderDetailContainer.style.display = 'block';
        });
    }
    // 상세정보 보기 버튼 클릭 시
    orderDetailButton.addEventListener('click', () => {
        clicked_order_item(orderId).then(order => {
            create_order_details(order);
            orderDetailContainer.style.display = 'block';
        });
    });
    // 리뷰 작성하기 버튼 클릭 시
    orderReviewButton.addEventListener('click', () => {
        clicked_order_item(orderId).then(order => {
            create_review_products(order);
            reviewContainer.style.display = 'block';
        });
    });
});
/************************************************/
// 주문 상세정보/리뷰 작성 버튼을 눌렀을 때
function clicked_order_item(orderId){
    return fetch(`/order/${orderId}`)
        .then(response => {
            // 하나의 주문 정보를 잘 가져왔음
            if(response.ok && response.status === 200){
                return response.json();
            }
            throw new Error();
        });
}
// 주문 상세정보/리뷰 닫기 버튼을 눌렀을 때
orderDetailCloseBtn.onclick = () => {
    orderDetailContainer.style.display = 'none';
    orderDetailTbody.innerHTML = ``;
}
// 주문했던 목록 생성하기
function create_order_details(order){
    orderDetailTbody.innerHTML = ``;
    const cartList = order.carts;
    for(let i = 0; i < cartList.length; i++){
        const cart = cartList[i];
        const cartAmount = cart.amount;
        const product = cart.product;
        const productName = product.name;
        const productPrice = product.price;
        const options = product.options;
        const image = product.images[0];
        let optionHTML = '';
        for(const option of options){
            optionHTML += `<div class="sub-title">${option.name}(+${option.price}원)</div>`;
        }

        orderDetailTbody.insertAdjacentHTML(`beforeend`,
            `<tr>
                <td class="product-no">${i+1}</td>
                <td class="product-img">
                    <img src="/image/${image.no}" alt="#">
                </td>
                <td class="product-summary">
                    <div class="title">${productName}</div>
                    ${optionHTML}
                </td>
                <td class="product-price">${productPrice}원</td>
                <td class="product-amount">${cartAmount}</td>
                ${i === 0 ? `<td id="product-total-price" rowspan="100">${order.totalPrice}</td>` : ''}
            </tr>`
        );
    }
}
/************************************************/
/// 리뷰 별 개수 선택하기
reviewStars.forEach((reviewStar, index) => {
    reviewStar.onclick = event => {
        for(let i = 0; i < reviewStars.length; i++){
            if(i <= index){
                reviewStars[i].className = "fa-solid fa-star";
            }else{
                reviewStars[i].className = "fa-regular fa-star";
            }
        }
    }
});
/// 리뷰 닫기 버튼 눌렀을 때
reviewCloseBtn.onclick = () => {
    reviewForm.action = ``;
    reviewContainer.style.display = 'none';

    for(let i = 0; i < reviewStars.length; i++){
        if(i === 0){
            reviewStars[i].className = "fa-solid fa-star";
        }else{
            reviewStars[i].className = "fa-regular fa-star";
        }
    }
}
/// 리뷰 작성 버튼 눌렀을 때
reviewWriteBtn.onclick = (event) => {
    if(!confirm('등록하시겠습니까?')){
        event.preventDefault();
        return;
    }
    if (reviewTextArea.value.trim() === ''){
        alert('내용은 필수값입니다!');
        event.preventDefault();
        return;
    }
    const rateInput = document.createElement('input');
    rateInput.type = 'hidden';
    rateInput.name = 'rate';
    rateInput.value = document.querySelectorAll('.review-form i.fa-solid').length;
    reviewForm.appendChild(rateInput);
}
// 주문했던 목록 생성하기
function create_review_products(order){
    reviewProductsSelect.innerHTML = ``;
    const cartList = order.carts;
    for(let i = 0; i < cartList.length; i++){
        const cart = cartList[i];
        const product = cart.product;
        const productNo = product.no;
        const productName = product.name;
        reviewProductsSelect.insertAdjacentHTML(`beforeend`,
            `<option value="${productNo}">${productName}</option>`
        );
    }
}