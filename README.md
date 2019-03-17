# Serverless Framework GUI
Integration of <a href="https://serverless.com/">Serverless Framework</a> for all JetBrains IDEs

The idea of the plugin in it's early stage is to ease and automate the tasks of switching between IDE/console while developing serverless applications with <a href="https://serverless.com/">Serverless Framework</a>.
The plugin adds a tool window at the right hand side of the IDE with label "Serverless Framework GUI". When you open the tool window you will see a tree where every first level element corresponds to single <b>serverless.yaml</b> or <b>serverless.yml</b> file found in the project and every second level element corresponds to a function found in the selected serverless config file.
You can "Deploy" and "Remove" a service by right clicking and selecting the appropriate action from the context menu. Double click on a function will invoke the function. Right click then selecting "Deploy and invoke" will automatically deploy the selected function prior invocation.
By default the function will be invoked without any input data. You have to create a file named serverless-framework-gui/service-name/function-name.json in order to be able to provide input data. The file path is relative to where the selected serverless config file resides.

###### Requirements
To use this plugin you must have bash shell and <a href="https://serverless.com/">Serverless Framework</a> installed.

###### Notes
<b>serverless</b> executable must be in your search path

<img src="https://s2.gifyu.com/images/serverless-framework-gui-overview.gif" border="0" />