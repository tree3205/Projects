saveToFolder(it) ::= <<
<li role="presentation">
    <a role="menuitem" tabindex="-1" href="/saveTo?type=$it.Folder$&mailID=$it.MailID$">$it.Folder$</a>
</li>
>>