row(it) ::= <<
<tr>
  <td $if(it.IfRead)$style="color:black; font-weight:bold"$else$style="color:grey; font-weight:bold"$endif$>$it.sender$</td>
  <td colspan="3"><a class="contentLink" href="/mail?id=$it.MailID$" $if(it.IfRead)$style="color:black; font-weight:bold"$else$style="color:grey; font-weight:bold"$endif$>$it.subject$</a></td>
  <td $if(it.IfRead)$style="color:black; font-weight:bold"$else$style="color:grey; font-weight:bold"$endif$>$it.date$</td>
</tr>
>>