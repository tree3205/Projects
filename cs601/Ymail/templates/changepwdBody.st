
changepwdBody() ::= <<
<div class="container">
    <div class="row">
        <div class="change col-lg-8 col-lg-offset-2">
            <form id="changepwdForm" method="post" class="form-horizontal" action="/changepwdSubmit">
                <h3 class="text-primary text-center">Change Password</h3>
                <hr>
                <div class="form-group">
                    <label class="col-lg-4 control-label">Current Password</label>
                    <div class="col-lg-5">
                        <input type="password" class="form-control" name="currentPWD" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-lg-4 control-label">New password</label>
                    <div class="col-lg-5">
                        <input type="password" class="form-control" name="newPWD" />
                    </div>
                </div>
                <div class="form-group">
                    <label class="col-lg-4 control-label">Retype password</label>
                    <div class="col-lg-5">
                        <input type="password" class="form-control" name="confirmPWD" />
                    </div>
                </div>
                <div class="form-group">
                    <div class="save col-lg-6 col-lg-offset-3">
                        <button type="submit" class="btn btn-primary">Save</button>
                    </div>
                <di></div>
                <div class="form-group">
                    <div class="save col-lg-6 col-lg-offset-3">
                        <button class="send btn btn-primary" type="button" onclick="location.href='/in?page=1';">Back</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
>>