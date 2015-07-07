alertBody(loginAlert, registerAlert, logoutAlert, changepwdAlert, pageAlert, type) ::= <<
$if(loginAlert)$
<div class="page-header">
    <h1 class="text-center text-primary">Sorry! Your email or password is not correct.</h1>
    <meta http-equiv="refresh" content="3;URL='/login'" />
</div>
$endif$

$if(registerAlert)$
<div class="page-header">
        <h1 class="text-center text-primary"> Oops! Your user name or email already exists.</h1>
        <h2 class="text-center text-primary"> Please select another one.</h2>
        <meta http-equiv="refresh" content="3;URL='/login'" />
    </div>
$endif$

$if(logoutAlert)$
<div class="page-header">
    <h1 class="text-center text-primary">You have safely logout</h1>
    <meta http-equiv="refresh" content="3;URL='/login'" />
</div>
$endif$

$if(changepwdAlert)$
<div class="page-header">
    <h1 class="text-center text-primary">Change password fail. Try again</h1>
    <meta http-equiv="refresh" content="3;URL='/in?page=1'" />
</div>
$endif$

$if(pageAlert)$
<div class="page-header">
    <h1 class="text-center text-primary">No such page in $type$</h1>
    <meta http-equiv="refresh" content="3;URL='/$type$page=1'" />
</div>
$endif$

>>