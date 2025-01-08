const orderUserSameButton = document.getElementById('order-user-same-button');
const ordererName = document.getElementById('order-name');
const ordererPhone = document.getElementById('order-phone');
const ordererEmail = document.getElementById('order-email');
const shippingOrdererSameButton = document.getElementById('shipping-orderer-same-button');
const shippingName = document.getElementById('shipping-name');
const shippingPhone = document.getElementById('shipping-phone');

const shippingPostCode = document.getElementById('shipping-postcode');
const shippingAddr = document.getElementById('shipping-addr');
const shippingAddrDetail = document.getElementById('shipping-addr-detail');
const shippingSearchButton = document.getElementById('shipping-search-button');

const orderButton = document.getElementById('order-button');

IMP.init("imp38408183");
/**********************************************/

orderUserSameButton.onclick = () => {
    ordererName.value = ordererName.getAttribute('data');
    ordererPhone.value = ordererPhone.getAttribute('data');
    ordererEmail.value = ordererEmail.getAttribute('data');
}

shippingOrdererSameButton.onclick = () => {
    if(shippingOrdererSameButton.checked){
        shippingName.value = ordererName.value;
        shippingPhone.value = ordererPhone.value;
    }
}

/**********************************************/

// daum 주소 찾기
const daumPostCode = new daum.Postcode({
    oncomplete: function(data) {
        // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분입니다.
        // 예제를 참고하여 다양한 활용법을 확인해 보세요.
        shippingPostCode.value = data.zonecode;
        shippingAddr.value = `${data.address} (${data.buildingName})`;
    }
});

// 주소 검색 버튼 클릭 시
shippingSearchButton.onclick = () => {
    daumPostCode.open();
}

/**********************************************/

// 주문하기 버튼을 클릭했을 시
orderButton.onclick = () => {
    // request_order(2, 1); // - DB 테스트용
    request_payment(); // - 실제 결제 용
};

// 주문에 필요한 주문 객체 생성
function create_order_obj(){
    // 고유 주문 번호 생성
    const uuid = crypto.randomUUID();
    const orderId = uuid.split('-')[0] + (new Date().getSeconds()).toString().padStart(2, '0');
    const products = document.getElementsByClassName('product');
    console.log(products)
    // 주문명 제작
    let orderName;
    const productName = products[0].querySelector('.product-title-container > b').textContent.substring(0, 8);
    console.log(productName)
    if(products.length === 1){ // 주문 상품이 하나라면
        orderName = `${productName}...`;
    }else{ // 주문 상품이 여러개라면
        orderName = `${productName}... 외 ${products.length - 1}개`;
    }

    // 주문 상품 모으기
    // 방법 1)
    // const cartList = [];
    // const products = document.getElementsByClassName('product');
    // for (const product of products){
    //     const [cartNo, cartAmount] = product.id.split('-')
    //     const cartDTOObject = {
    //         no: +cartNo,
    //         amount: +cartAmount
    //     }
    //     cartList.push(cartDTOObject)
    // }
    // 방법 2)
    // const cartList = [...document.getElementsByClassName('product')]
    //     .map(product => {
    //         const [cartNo, cartAmount] = product.id.split('-');
    //         // console.log(product.id);
    //         // console.log(cartNo);
    //         // console.log(cartAmount);
    //         const cartObject = {};
    //         if(cartNo !== 'null'){
    //             cartObject['no'] = +cartNo;
    //         }
    //         cartObject['amount'] = +cartAmount;
    //         return cartObject;
    //     });
    // 방법 2) -> 축약
    // const cartList = [...document.getElementsByClassName('product')]
    //     .map(product => ({
    //         no: +product.id.split('-')[0],
    //         amount: +product.id.split('-')[1]
    //     }));
    // -> 더 축약
    // const cartList = [...document.getElementsByClassName('product')]
    //     .map(product => ({no: +product.id.split('-')[0], amount: +product.id.split('-')[1]}));
    // orderObject['carts'] = cartList;

    return {
        id: orderId,
        name: orderName,
        shippingName: shippingName.value,
        shippingTel: shippingPhone.value,
        shippingAddr: shippingAddr.value,
        shippingAddrDetail: shippingAddrDetail.value,
        shippingPostcode: shippingPostCode.value,
        ordererName: ordererName.value,
        ordererTel: ordererPhone.value,
        ordererEmail: ordererEmail.value
    }
}

// 결제 요청 (Portone)
function request_payment() {
    const orderObject = create_order_obj();
    const totalPrice = +document.querySelector('.order-total-price-container').getAttribute('data');
    console.log('주문내용: ', orderObject);

    IMP.request_pay({
            channelKey: "channel-key-791f83bd-c692-4304-a821-4b7426e02177",
            pg: "kakaopay",
            merchant_uid: orderObject.id, // 주문 고유 번호
            currency: "KRW",
            name: orderObject.name,
            amount: totalPrice,
            buyer_email: orderObject.ordererEmail,
            buyer_name: orderObject.ordererName,
            buyer_tel: orderObject.ordererTel,
            // buyer_addr: "서울특별시 강남구 신사동",
            // buyer_postcode: "01181",
        },
        function (response) {
            // 결제 종료 시 호출되는 콜백 함수
            // response.imp_uid 값으로 결제 단건조회 API를 호출하여 결제 결과를 확인하고,
            // 결제 결과를 처리하는 로직을 작성합니다.
            console.log('결제요청결과:', response);
            if (response.error_code != null) {
                return alert(`결제에 실패하였습니다. 에러 내용: ${response.error_msg}`);
            }
            if (!response['success']){
                alert('사용자가 결제를 취소했습니다.');
            }
            // 결제 성공이라면 우리 어플리케이션으로 주문 요청하기
            orderObject['impUid'] = response['imp_uid'];
            request_order(orderObject);
        },
    );
}

// 주문 요청 (우리 어플리케이션)
function request_order(requestBody){
    console.log(JSON.stringify(requestBody));
    const csrfToken = document.querySelector('meta[name=_csrf]').getAttribute('content');
    // 우리 어플리케이션으로 요청
    fetch(`/order`, {
        method: "POST",
        headers: {
            "Content-Type":"application/json",
            "X-CSRF-TOKEN": csrfToken,
        },
        body: JSON.stringify(requestBody)
    }).then(response => {
        if(response.ok && response.status === 201){
            alert('주문이 완료되었습니다!');
            // 주문완료 페이지로 이동
            location.href = '/user/order/complete';
        }
    });
}