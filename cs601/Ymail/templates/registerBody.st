registerBody() ::= <<
    <div class="container">
        <div class="row">
            <div class="col-lg-8 col-lg-offset-2">
                <div class="page-header">
                    <img class="welcome" src="/files/templates/Welcome.png">
                </div>
                <form id="registerForm" method="post" class="form-horizontal" action="/registerSubmit">
                    <div class="form-group">
                        <label class="col-lg-3 control-label">Username</label>
                        <div class="col-lg-5">
                            <input type="text" class="form-control" name="username" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-lg-3 control-label">WebMail Password</label>
                        <div class="col-lg-5">
                            <input type="password" class="form-control" name="webmailPwd" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-lg-3 control-label">Retype password</label>
                        <div class="col-lg-5">
                            <input type="password" class="form-control" name="confirmWebMailPassword" />
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-lg-9 col-lg-offset-3">
                            <button type="submit" class="btn btn-primary">Sign up</button>
                        </div>
                        <div class="col-lg-9 col-lg-offset-5">
                                <button class="send btn btn-primary" type="button" onclick="location.href='/in?page=1';">Back</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>
>>