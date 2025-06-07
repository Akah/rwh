Title: Transposing selections in Emacs
Date: 2025/04/02
Tags: emacs lisp snippet
Name: transposing

## Transposing selections in Emacs
####*2025/04/02*

For those not in the know, Emacs has a group of commands for [transposing text](https://www.gnu.org/software/emacs/manual/html_node/emacs/Transpose.html). They're a delightful collection of useful text editing commands used to swap characters, words, sentences and more. `C-t` being perhaps the most useful for correcting typos while writing. However there was one use-case that I felt was missing: Transposing two regions around a user-defined point.

Take for example the JavaScript ternary statement:
```javascript
const val = foo ? bar : baz;
```
It's common for logic to need to change and for the expressions to need to swap. So instead of: `bar : baz` we want: `baz : bar`.

Thanks to the power of lisp it was easy to write a function to do exactly this:
```lisp
(defun swap-substrings (start end separator)
  "Swap substrings around the selected region separated by SEPARATOR."
  (interactive "r\nMEnter separator string: ")
  (save-excursion
    (goto-char start)
    (when (search-forward separator end t)
      (let ((first-substring (buffer-substring start (match-beginning 0)))
            (second-substring (buffer-substring (match-end 0) end)))
        (delete-region start end)
        (insert second-substring)
        (insert separator)
        (insert first-substring)))))
```
As a brief summary:
 - The function swaps the positions of two substrings around a specified separator in the selected region of the buffer.
 - It searches for the separator in the selected region.
 - If found, it swaps the substrings before and after the separator.
 - Otherwise the function exits quietly.

Now using this function we select the area we want to transpose, call our function, enter the string we want to use for the seperator in this case `_:_`, and watch in wonder as emacs swaps the two expressions around.

This function has plenty more use cases: swaping parameter orders in functions.

*Tags: [#emacs](/blog/tag/emacs) [#lisp](/blog/tag/lisp) [#snippet](/blog/tag/snippet)*
