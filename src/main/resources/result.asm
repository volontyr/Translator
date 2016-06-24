Data Segment
D db ?
A dd 1
Error: integer type of constant expected
B dd 2147483648
Error: integer type of constant expected
C dd -2147483649
Data ends
Code segment
start:
	mov V1, -25
	mov V2, 17
Code ends
end start