document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("searchForm");
    const keyword = document.getElementById("sideSearchKeyword");

    form.addEventListener("submit", function (e) {
        if (keyword.value.trim() === "") {
            e.preventDefault();
            alert("검색어를 입력하세요!");
            keyword.focus();
        }
    });
});
