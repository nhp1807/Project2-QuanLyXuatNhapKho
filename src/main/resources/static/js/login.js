function login() {
    var email = document.getElementById('tenDangNhap').value;
    var password = document.getElementById('matKhau').value;

    if (email === 'admin' && password === 'admin') {
        // Redirect to the home.html on successful login.
        window.location.href = 'ad_dstk.html';
    } else {
        alert('Tên đăng nhập hoặc mật khẩu không đúng');
    }
}

function register(){
    window.location.href = 'login.html';
}