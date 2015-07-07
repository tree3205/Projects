loginBody() ::= <<
    <div class="mail">
        <img src="/files/templates/icon-mail-light.png" class="img-responsive" alt="Responsive image" align="left" style="float:inherit;width:126px;height:126px">
    </div>
    <h1 class="text-center">Welcome to Web Mail</h1>
    <div class="container">
        <form class="form-signin" method="post" action="/loginSubmit">
            <h2 class="form-signin-heading">Please sign in</h2>
            <label for="inputEmail" class="sr-only">Email address</label>
            <input type="text" id="inputEmail" class="form-control" placeholder="Email address" name="username" autofocus="">
            <label for="inputPassword" class="sr-only">Password</label>
            <input type="password" id="inputPassword" class="form-control" placeholder="Password" name="password" >
            <button class="btn btn-lg btn-primary btn-block" type="submit">Sign in</button>
            <p class="text-center"> Not a member now?
                <!-- should be change to register page -->
                <a href="/register">Sign up</a>
            </p>
        </form>
    </div>
    <!-- /container -->
    <!-- Latest compiled and minified JavaScript -->
    <script src="https://code.jquery.com/jquery-2.1.3.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
>>