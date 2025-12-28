package com.example.to_dolist;


public class TaskItem  {


        private String taskName;
        private String dueDate;
        private String dueTime;

        public TaskItem(String taskName, String dueDate, String dueTime) {
            this.taskName = taskName;
            this.dueDate = dueDate;
            this.dueTime = dueTime;
        }
 
        public String getTaskName() {
            return taskName;
        }

        public String getDueDate() {
            return dueDate;
        }

        public String getDueTime() {
            return dueTime;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        public void setDueTime(String dueTime) {
            this.dueTime = dueTime;
        }
    }