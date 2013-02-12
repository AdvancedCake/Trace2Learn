Trace2Learn
===========

How to import to Eclipse
------------------------
_(By Seunghoon Park, pclove1@gmail.com, Dec 7th 2012)_

1. right-click on the package explorer in Eclipse

2. select "Import..."

3. select "Android/Existing Android Code Into Workspace"

4. configure the "Root Directory" by clicking "Browse..."
   to this repo's root

5. Now, you should be able to see 4 detected projects.
   please 'un-select' MainMenuActivity from TraceMe.
   (Note:
   This is because Eclipse/Android don't use a smart way to specify the project's name.
   You'll change the project name manually soon.)

6. While selecting the rest of three projects, click "Finish"

7. Now, let's change the projects' names.
   Click a project that you want change its name on the package explorer.
   Then, push 'F2'.
   Recommended projects' names:

    * CharacterCreationActivity -> TraceLibrary
    * MainMenuActivity(from Trace2Learn directory) -> Trace2Learn
    * tests -> Trace2Learn_tests

8. Now, let's import the simplified app as well.
   Repeat 1-5 steps.
   At 5th step, select MainMenuActivity from TraceMe.
   You can change its project name as the above.

9. add Java build path to Trace2Learn_tests project.
   right click on the Trace2Learn_tests(or tests, if you didn't change)
   click "Properties"
   click "Java Build Path" on the left column.
   select "Projects" tab.
   click "Add..."
   select Trace2Learn(or MainMenuActivity from Trace2Learn directory)
