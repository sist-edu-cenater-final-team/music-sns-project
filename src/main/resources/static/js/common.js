const asideNavigation = document.querySelector(".navigation-list");
const asideBtnList = asideNavigation.querySelectorAll(".btn");
const asideLayer = document.querySelectorAll(".aside-navigation-layer");
asideBtnList.forEach(btn => {

    const dataTarget = btn.dataset.target;

    if(!dataTarget) return;

    btn.addEventListener("click", () => {

        let thisLayer = document.querySelector("#"+dataTarget);
        let isLayer = document.querySelector("#"+dataTarget).classList.contains("on");

        if(isLayer){
            thisLayer.classList.remove("on");
            btn.classList.remove("active");
        }
        else {
            asideLayer.forEach(layer => layer.classList.remove("on"));
            asideBtnList.forEach(item2 => item2.classList.remove("active"));
            btn.classList.add("active");
            thisLayer.classList.add("on");
        }
    });

});

// 영역 외 클릭 시 팝업 닫기
document.addEventListener("click", (e) => {
    let isClickInside = [...asideNavigation.querySelectorAll(".btn"), ...asideLayer]
        .some(el => el.contains(e.target));

    if (!isClickInside) {
        asideLayer.forEach(layer => layer.classList.remove("on"));
        asideBtnList.forEach(btn => btn.classList.remove("active"));
    }
});

//
// // 메시지 팝업 관련
// const talkLayer = {
//     layer : document.querySelector("#talkLayer"),
//     btnTalk : document.querySelector("#btnTalk"),
//     btnTalkClose : document.querySelector("#btnTalkClose"),
//     open() {
//         this.layer.style.display = "block";
//     },
//     close() {
//         this.layer.style.display = "none";
//     }
// }
//
// talkLayer.btnTalk?.addEventListener("click", () => talkLayer.open());
// talkLayer.btnTalkClose?.addEventListener("click", () => talkLayer.close());

// 우측 감정 플레이리스트
const emotions = document.querySelector(".emotions");
emotionBtnList = emotions.querySelectorAll(".btn");
emotionBtnList.forEach(btn => {

    btn.addEventListener("click", () => {
        emotionBtnList.forEach(item => item.classList.remove("active"));
        btn.classList.add("active");
    });
});



document.addEventListener('DOMContentLoaded', () => {
    let notificationSound;
    let soundInitialized = false;

    function initSound() {
        if (soundInitialized) return; // 이미 실행된 경우 방지
        soundInitialized = true;

        notificationSound = new Audio('data:audio/wav;base64,UklGRnoGAABXQVZFZm10IBAAAAABAAEAQB8AAEAfAAABAAgAZGF0YQoGAACBhYqFbF1fdJivrJBhNjVgodDbq2EcBj+a2/LDciUFLIHO8tiJNwgZaLvt559NEAxQp+PwtmMcBjGH0fPTgjMGHm7A7+OZURE=');
        notificationSound.volume = 0.3;

        notificationSound.play().then(() => {
            notificationSound.pause();
            notificationSound.currentTime = 0;
            console.log("알림음 권한 확보됨");
        }).catch(err => {
            console.log("알림음 초기화 실패:", err);
        });

        // 권한 확보 후 이벤트 제거 (불필요한 실행 방지)
        document.removeEventListener("click", initSound);
        document.removeEventListener("keydown", initSound);
    }

// 어떤 버튼 클릭이든, 키보드 입력이든 처음 1번만 initSound 실행
    document.addEventListener("click", initSound);
    document.addEventListener("keydown", initSound);
});

