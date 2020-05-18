import tkinter as tk
from tkinter.filedialog import askopenfilename, asksaveasfilename
from tkinter.filedialog import askdirectory
import os
import io
import time

def millis():
    return int(time.time() * 1000)

labeling_directory = ''
labeling_files = []
file_cursor = 0

def load_files():
    global labeling_directory
    global labeling_files

    print('Loading files')

    labeling_files = []

    for file in os.listdir(labeling_directory):
        if 'label' not in file:
            labeling_files.append(labeling_directory + file)

    next_file()

def next_file():
    global file_cursor

    if file_cursor > 0:
        #save the file
        file_path = labeling_files[file_cursor - 1]
        with open(file_path.replace('.txt', '_label.txt'), 'w') as label_file:
            label_file.write(txt_edit2.get(1.0, tk.END))

    peeking_file(txt_edit2.get(1.0, tk.END))

    # print(labeling_files[file_cursor])
    # with io.open(labeling_files[file_cursor], 'r', encoding='utf-8') as labeling_file:
    #     txt = labeling_file.read()
    txt = eval(open(labeling_files[file_cursor], 'r').read())
    txt_text.delete('1.0', tk.END)
    txt_text.insert(tk.END, txt[0])

    # txt_desc.delete('1.0', tk.END)
    # if len(txt[1]) > 500:
    #     txt[1] = txt[1][:300]
    # txt_desc.insert(tk.END, txt[1])

    txt_edit2.delete(1.0, tk.END)
    if os.path.exists(labeling_files[file_cursor].replace('.txt', '_label.txt')):
        with open(labeling_files[file_cursor].replace('.txt', '_label.txt'), 'r') as label_file:
            txt_edit2.insert(tk.END, label_file.read())

    file_cursor += 1
    
def peeking_file(text):
    
    with open('./peeking.txt', 'r') as job_file:
        jobs = job_file.read()
    jobs = jobs.split('\n')

    if text is not None:
        job = text.split('\n')[0]
    else:
        job = ''
        
    if job not in jobs:
        jobs.append(job)
    else:
        print('job existed')
    
    strr = ''
    for job in jobs:
        strr += job
        strr += '\n'
    strr = strr[:-1]
    print(millis())
    print(strr)

    with open('./peeking.txt', 'w') as job_file:
        job_file.write(strr)

    txt_labeled.delete(1.0, tk.END)
    txt_labeled.insert(tk.END, strr)

def open_folder():
    global labeling_directory
    filepath = askdirectory(
        # filetypes=[("Text Files", "*.txt"), ("All Files", "*.*")]
    )
    if not filepath:
        return
    
    labeling_directory = filepath + '/'
    load_files()
    # print(labeling_directory)
    # txt_edit.delete(1.0, tk.END)
    # with open(filepath, "r") as input_file:
    #     text = input_file.read()
    #     txt_edit.insert(tk.END, text)
    window.title(f"Simple Text Editor - {filepath}")
    peeking_file(None)

def save_file():
    """Save the current file as a new file."""
    filepath = asksaveasfilename(
        defaultextension="txt",
        filetypes=[("Text Files", "*.txt"), ("All Files", "*.*")],
    )
    if not filepath:
        return
    with open(filepath, "w") as output_file:
        text = txt_edit.get(1.0, tk.END)
        output_file.write(text)
    window.title(f"Simple Text Editor - {filepath}")

window = tk.Tk()
window.title("Simple Text Editor")
window.rowconfigure(0, minsize=400, weight=1)
window.columnconfigure(1, minsize=400, weight=1)

txt_text = tk.Text(window)
txt_desc = tk.Text(window)
txt_edit2 = tk.Text(window)
txt_labeled = tk.Text(window)
fr_buttons = tk.Frame(window, relief=tk.RAISED, bd=2)
btn_open = tk.Button(fr_buttons, text="Open", command=open_folder)
btn_save = tk.Button(fr_buttons, text="Save", command=save_file)
btn_next = tk.Button(fr_buttons, text="Next", command=next_file)

btn_open.grid(row=0, column=0, sticky="ew", padx=5, pady=5)
btn_save.grid(row=1, column=0, sticky="ew", padx=5)
btn_next.grid(row=2, column=0, sticky="ew", padx=5, pady=5)

fr_buttons.grid(row=0, column=0, sticky="ns")
txt_text.grid(row=0, column=1, sticky="nsew")
txt_desc.grid(row=0, column=2, sticky="nsew")
txt_edit2.grid(row=1, column=1, sticky="nsew")
txt_labeled.grid(row=1, column=2, sticky="nsew")

window.mainloop()