addBody() ::= <<
    <div class="container">
        <div class="row">
            <div class="col-lg-8 col-lg-offset-2">
                <form id="addMailForm" method="post" class="form-horizontal" action="/addSubmit">
                    <div class="page-header">
                        <h1 class="text-primary">Welcome to use web mail</h1>
                    </div>
                    <div class="form-group">
                        <label class="col-lg-3 control-label">Your email address</label>
                        <div class="col-lg-5">
                            <input type="text" class="form-control" name="email" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-lg-3 control-label">SMTP Server</label>
                        <div class="col-lg-5">
                            <input type="text" class="form-control" name="smtp" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-lg-3 control-label">POP Server</label>
                        <div class="col-lg-5">
                            <input type="text" class="form-control" name="pop" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-lg-3 control-label">SMTP Port</label>
                        <div class="col-lg-5">
                            <input type="text" class="form-control" name="smtpport" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-lg-3 control-label">POP Port</label>
                        <div class="col-lg-5">
                            <input type="text" class="form-control" name="popport"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-lg-3 control-label">Password</label>
                        <div class="col-lg-5">
                            <input type="password" class="form-control" name="password" />
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-lg-3 control-label">Retype password</label>
                        <div class="col-lg-5">
                            <input type="password" class="form-control" name="confirmPassword" />
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-lg-9 col-lg-offset-3">
                            <button type="submit" class="btn btn-primary">Sign up</button>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-lg-9 col-lg-offset-3">
                            <button class="btn btn-primary" type="button" onclick="location.href='/in?page=1';">Back</button>
                        </div
                    </div>
                </form>
            </div>
        </div>
    </div>
>>