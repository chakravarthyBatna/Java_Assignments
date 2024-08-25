const inputBox = document.getElementById('input-box');
const taskDueDate = document.getElementById('task-due-date');
const taskDueTime = document.getElementById('task-due-time');
const taskPriority = document.getElementById('task-priority');
const listContainer = document.getElementById('list-container');
const completedListContainer = document.getElementById('completed-list-container');
const searchBar = document.getElementById('search-bar');
const filterTasks = document.getElementById('filter-tasks');
const darkModeToggle = document.getElementById('dark-mode-toggle');
const sortDueDate = document.getElementById('sort-due-date');
const sortPriority = document.getElementById('sort-priority');
showAllTasks();
initializeSortable();

sortPriority.addEventListener('change', showAllTasks); //showAllTasks called when we use the sort by priority
sortDueDate.addEventListener('change', showAllTasks);  //showAllTasks called when we use the sort by due-date
async function addTask() {
    // Get form fields
    const taskName = inputBox.value.trim();
    const dueDate = taskDueDate.value;
    const dueTime = taskDueTime.value;
    const priority = taskPriority.value;

    // Validate task name
    if (!taskName) {
        alert('You must write something for a task.');
        return;
    }

    // Check for duplicate tasks
    if (checkDuplicate(taskName, dueDate, dueTime)) {
        alert('Duplicate task found. Please enter a unique task.');
        return;
    }

    const task = {
        taskName: taskName,
        dueDate: dueDate,
        dueTime: dueTime,
        priority: priority,
        completed: false
    };

    try {
        // Call the API
        const response = await fetch('http://localhost:8080/TodoApp/tasks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(task)
        });

        if (response.ok) {
            const createdTask = await response.json();
            console.log('Task created:', createdTask);

        } else {
            console.error('Failed to create task:', response.statusText);
        }
    } catch (error) {
        console.error('Error:', error);
    }

    // Schedule notification
    if (dueDate && dueTime) {
        scheduleNotification(taskName, dueDate, dueTime);
    }

// Show updated task list
    showAllTasks();

    // Clear input fields
    inputBox.value = '';
    taskDueDate.value = '';
    taskDueTime.value = '';
    taskPriority.value = 'low';
}


function checkDuplicate(taskName, dueDate, dueTime) {
    const tasks = JSON.parse(localStorage.getItem('tasks')) || [];
    return tasks.some(task =>
        task.taskName === taskName
    );
}
function showAllTasks() {
    // Get the values from select elements
    const filterTasksValue = document.getElementById('filter-tasks').value;
    const sortPriorityValue = document.getElementById('sort-priority').value;
    const sortDueDateValue = document.getElementById('sort-due-date').value;

    // Convert priority filter to a number
    let priorityNumber = '';
    if (filterTasksValue === 'high') {
        priorityNumber = 1;
    } else if (filterTasksValue === 'medium') {
        priorityNumber = 2;
    } else if (filterTasksValue === 'low') {
        priorityNumber = 3;
    }

    // Construct the URL with query parameters based on selected values
    let url = 'http://localhost:8080/TodoApp/tasks';

    let params = [];
    if (priorityNumber) {
        params.push(`priority=${priorityNumber}`);
    }

    if (sortPriorityValue !== 'none') {
        params.push(`sortBy=PRIORITY&sortOrder=${sortPriorityValue}`);
    }

    if (sortDueDateValue !== 'none') {
        params.push(`sortBy=DUE_DATE&sortOrder=${sortDueDateValue}`);
    }

    if (params.length > 0) {
        url += '?' + params.join('&');
    }

    // Fetch tasks from the server
    fetch(url)
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.message || 'Network response was not ok');
                });
            }
            return response.json(); // Parse JSON from the response
        })
        .then(tasks => {
            // Assuming tasks is an array of task objects
            showPendingTasks(tasks.filter(task => !task.completed));
            showCompletedTasks(tasks.filter(task => task.completed));
        })
        .catch(error => {
            console.error('There was a problem with the fetch operation:', error);
            alert('Error fetching tasks: ' + error.message);
        });
}


function showCompletedTasks(tasks) {
    const completedListContainer = document.getElementById('completed-list-container');
    completedListContainer.innerHTML = ''; // Clear any existing content
    const listItems = buildHtmlForEachCompletedTask(tasks);
    listItems.forEach(item => completedListContainer.appendChild(item));
      initializeSortable();  // Drag and drop
        initializeEllipsisMenu();  // Ellipsis menu (three dots)
        initializeEditDeleteEvents(); // Edit and delete buttons
        initializeDetailsEvent();
}

function buildHtmlForEachCompletedTask(tasks) {
    const listItems = [];

    tasks.forEach(task => {
        const listItem = document.createElement('li');
        listItem.classList.add('list-group-item', 'task-item');
        // Set task UUID as a hidden div
        const taskId = document.createElement('div');
        taskId.style.display = 'none'; // This hides the element
        taskId.textContent = task.taskId;
        listItem.appendChild(taskId);

        switch (task.priority) {
            case 'low':
                listItem.classList.add('task-priority-low');
                break;
            case 'medium':
                listItem.classList.add('task-priority-medium');
                break;
            case 'high':
                listItem.classList.add('task-priority-high');
                break;
            default:
                listItem.classList.add('task-priority-low');
        }

        const ellipsis = document.createElement('span');
        ellipsis.classList.add('ellipsis-menu');
        ellipsis.innerHTML = '&#8226;&#8226;&#8226;';

        const dropdownMenu = document.createElement('div');
        dropdownMenu.classList.add('dropdown-menu');
        dropdownMenu.innerHTML = `
            <a href="#" class="edit-task">Edit</a>
            <a href="#" class="delete-task">Delete</a>
            <a href="#" class="task-details">Details</a>
            <a href="#" class="markAsComplete">Mark As Complete</a>
            <a href="#" class="add-subtask">Add Subtask</a>
        `;

        listItem.innerHTML = `
           <div style="display: none;">${task.taskId}</div>
            <div class="task-name">${task.taskName}</div>
            <div class="task-due-date-time">
                <span class="due-info">${task.dueDate}</span>
                <span class="due-info">${task.dueTime}</span>
            </div>
        `;

        // Check if there are any subtasks
        if (task.subtasks && task.subtasks.length > 0) {
            const subtaskList = document.createElement('ul');
            subtaskList.classList.add('subtask-list');

            task.subtasks.forEach((subtask, index) => {
                const subtaskItem = document.createElement('li');
                subtaskItem.classList.add('subtask-item');
                subtaskItem.innerHTML = `
                    <div class="subtask-name">${subtask.subtaskName}</div>
                     <div class="subtask-due-date-time">
                        <span class="due-info">${subtask.subtaskDueDate}</span>
                        <span class="due-info">${subtask.subtaskDueTime}</span>
                     </div>
                `;
                subtaskList.appendChild(subtaskItem);
            });

            listItem.appendChild(subtaskList);
        }

        listItem.appendChild(ellipsis);
        listItem.appendChild(dropdownMenu);
        listItems.push(listItem); // Collect the list item
        attachAddSubtaskEvent(listItem);
    });

    return listItems; // Return all list items as an array
}

function showPendingTasks(tasks) {
    listContainer.innerHTML = '';

    // Get sort order from select elements
    const sortPriorityValue = sortPriority.value;
    const sortDueDateValue = sortDueDate.value;

    buildHtmlForEachPendingTask(tasks);

    // Re-initialize functionalities for the new task items
    initializeSortable();  // Drag and drop
    initializeEllipsisMenu();  // Ellipsis menu (three dots)
    initializeEditDeleteEvents(); // Edit and delete buttons
    initializeDetailsEvent(); // Details button
}

function buildHtmlForEachPendingTask(tasks) {
    // Build the HTML for each task and append it to the UI
    tasks.forEach(task => {
        const listItem = document.createElement('li');
        listItem.classList.add('list-group-item', 'task-item');
        // Set task UUID as a hidden div
        const taskId = document.createElement('div');
        taskId.style.display = 'none'; // This hides the element
        taskId.textContent = task.taskId;
        listItem.appendChild(taskId);

        switch (task.priority) {
            case 'low':
                listItem.classList.add('task-priority-low');
                break;
            case 'medium':
                listItem.classList.add('task-priority-medium');
                break;
            case 'high':
                listItem.classList.add('task-priority-high');
                break;
            default:
                listItem.classList.add('task-priority-low');
        }

        const ellipsis = document.createElement('span');
        ellipsis.classList.add('ellipsis-menu');
        ellipsis.innerHTML = '&#8226;&#8226;&#8226;';

        const dropdownMenu = document.createElement('div');
        dropdownMenu.classList.add('dropdown-menu');
        dropdownMenu.innerHTML = `
            <a href="#" class="edit-task">Edit</a>
            <a href="#" class="delete-task">Delete</a>
            <a href="#" class="task-details">Details</a>
            <a href="#" class="markAsComplete">Mark As Complete</a>
            <a href="#" class="add-subtask">Add Subtask</a>
        `;

        listItem.innerHTML = `
           <div style="display: none;">${task.taskId}</div>
            <div class="task-name">${task.taskName}</div>
            <div class="task-due-date-time">
                <span class="due-info">${task.dueDate}</span>
                <span class="due-info">${task.dueTime}</span>
            </div>
        `;

        // Check if there are any subtasks
        if (task.subtasks && task.subtasks.length > 0) {
            const subtaskList = document.createElement('ul');
            subtaskList.classList.add('subtask-list');

            task.subtasks.forEach((subtask, index) => {
                const subtaskItem = document.createElement('li');
                subtaskItem.classList.add('subtask-item');
                subtaskItem.innerHTML = `
                    <div class="subtask-name">${subtask.subtaskName}</div>
                     <div class="subtask-due-date-time">
                        <span class="due-info">${subtask.subtaskDueDate}</span>
                        <span class="due-info">${subtask.subtaskDueTime}</span>
                     </div>
                `;
                subtaskList.appendChild(subtaskItem);
            });

            listItem.appendChild(subtaskList);
        }

        listItem.appendChild(ellipsis);
        listItem.appendChild(dropdownMenu);
        listContainer.appendChild(listItem);
        attachAddSubtaskEvent(listItem);
    });
}

function attachAddSubtaskEvent(listItem) {
    listItem.querySelector('.add-subtask').onclick = function (event) {
        event.preventDefault();
        openSubtaskDialog(listItem);
    };
}

function openSubtaskDialog(taskItem) {
    const dialog = document.createElement('div');
    dialog.classList.add('dialog');

    const taskNameElement = taskItem.querySelector('.task-name');
    const dueDateElement = taskItem.querySelector('.task-due-date-time .due-info:nth-child(1)');
    const dueTimeElement = taskItem.querySelector('.task-due-date-time .due-info:nth-child(2)');

    const taskName = taskNameElement ? taskNameElement.textContent.trim() : '';
    const dueDate = dueDateElement ? dueDateElement.textContent.trim() : '';
    const dueTime = dueTimeElement ? dueTimeElement.textContent.trim() : '';

    dialog.innerHTML = `
        <h3>Add Subtask</h3>
        <label for="subtask-name">Subtask Name:</label>
        <input type="text" id="subtask-name" placeholder="Enter subtask name">
        <label for="subtask-due-date">Due Date:</label>
        <input type="date" id="subtask-due-date">
        <label for="subtask-due-time">Due Time:</label>
        <input type="time" id="subtask-due-time">
        <button id="confirm-add-subtask">Add</button>
        <button id="cancel-add-subtask">Cancel</button>
    `;

    document.body.appendChild(dialog);

    const confirmButton = dialog.querySelector('#confirm-add-subtask');
    const cancelButton = dialog.querySelector('#cancel-add-subtask');

    confirmButton.addEventListener('click', function () {
        const subtaskName = dialog.querySelector('#subtask-name').value.trim();
        const subtaskDueDate = dialog.querySelector('#subtask-due-date').value;
        const subtaskDueTime = dialog.querySelector('#subtask-due-time').value;

        if (subtaskName === '') {
            alert('Subtask Name is required.');
            return;
        }

        // Get the task details
        const taskNameElement = taskItem.querySelector('.task-name');
        const dueDateElement = taskItem.querySelector('.task-due-date-time .due-info:nth-child(1)');
        const dueTimeElement = taskItem.querySelector('.task-due-date-time .due-info:nth-child(2)');

        const taskName = taskNameElement ? taskNameElement.textContent.trim() : '';
        const dueDate = dueDateElement ? dueDateElement.textContent.trim() : '';
        const dueTime = dueTimeElement ? dueTimeElement.textContent.trim() : '';
        document.body.removeChild(dialog);
        addSubtaskToTask(taskName, dueDate, dueTime, subtaskName, subtaskDueDate, subtaskDueTime);

    });

    cancelButton.addEventListener('click', function () {
        document.body.removeChild(dialog);
    });
}


function addSubtaskToTask(taskName, dueDate, dueTime, subtaskName, subtaskDueDate, subtaskDueTime) {

    addSubtaskToLocalStorage(taskName, dueDate, dueTime, subtaskName, subtaskDueDate, subtaskDueTime);

    showAllTasks();
}


function addSubtaskToLocalStorage(taskName, dueDate, dueTime, subtaskName, subtaskDueDate, subtaskDueTime) {
    // Retrieve the tasks from localStorage
    let tasks = JSON.parse(localStorage.getItem('tasks')) || [];


    // Find the task in localStorage
    tasks = tasks.map(task => {
        if (task.taskName === taskName) {
            // Add the new subtask
            task.subtasks.push({
                subtaskName: subtaskName,
                subtaskDueDate: subtaskDueDate,
                subtaskDueTime: subtaskDueTime
            });
        }
        return task;
    });

    // Save the updated tasks back to localStorage
    localStorage.setItem('tasks', JSON.stringify(tasks));
}


function initializeDetailsEvent() {
    const taskItems = listContainer.querySelectorAll('.task-item');
    taskItems.forEach(listItem => {
        listItem.querySelector('.task-details').onclick = function (event) {
            event.preventDefault();
            showDetailsDialog(listItem);
        };
    });
}

async function showDetailsDialog(listItem) {
    // Retrieve the taskId from the hidden div
    const taskIdElement = listItem.querySelector('div[style="display: none;"]');
    const taskId = taskIdElement ? taskIdElement.textContent.trim() : null;

    if (!taskId) {
        console.error('Task ID not found.');
        return;
    }

    try {
        // Fetch task details from the API
        const response = await fetch(`http://localhost:8080/TodoApp/tasks?taskId=${taskId}`);
        if (!response.ok) {
            throw new Error('Failed to fetch task details');
        }
        const task = await response.json();

        // Extract task details from the API response
        const taskName = task.taskName;
        const taskDueDate = task.dueDate;
        const taskDueTime = task.dueTime;
        const priority = task.priority.toLowerCase();

        // Create the details dialog
        const detailsDialog = document.createElement('div');
        detailsDialog.classList.add('details-dialog');
        detailsDialog.innerHTML = `
            <div class="details-dialog-content">
                <h3>Task Details</h3>
                <p><strong>Task Name:</strong> ${taskName}</p>
                <p><strong>Due Date:</strong> ${taskDueDate}</p>
                <p><strong>Due Time:</strong> ${taskDueTime}</p>
                <p><strong>Priority:</strong> ${priority}</p>
                <button id="close-details" class="btn btn-secondary">Go Back</button>
            </div>
        `;

        // Create overlay
        const overlay = document.createElement('div');
        overlay.classList.add('overlay');

        // Append overlay and dialog to the body
        document.body.appendChild(overlay);
        document.body.appendChild(detailsDialog);

        // Set display to block
        detailsDialog.style.display = 'block';
        overlay.style.display = 'block';

        // Add event listener to close button
        document.getElementById('close-details').onclick = function () {
            document.body.removeChild(detailsDialog);
            document.body.removeChild(overlay);
        };
    } catch (error) {
        console.error('Error fetching task details:', error);
    }
}



// closes all the dropdown menus if clicked outside
document.addEventListener('click', function (event) {
    if (!event.target.closest('.list-group-item')) {
        document.querySelectorAll('.dropdown-menu').forEach(menu => menu.style.display = 'none');
    }
});

function attachEllipsisEvent(ellipsis, listItem) {
    ellipsis.onclick = function (event) {
        event.stopPropagation();
        const menu = listItem.querySelector('.dropdown-menu');
        const isVisible = menu.style.display === 'block';
        document.querySelectorAll('.dropdown-menu').forEach(menu => menu.style.display = 'none');
        menu.style.display = isVisible ? 'none' : 'block';
    };
}
function showEditDialog(listItem) {
    // Retrieve the task ID from the hidden div
    const taskIdElement = listItem.querySelector('div[style="display: none;"]');
    const taskId = taskIdElement ? taskIdElement.textContent.trim() : null;

    const taskNameElement = listItem.querySelector('.task-name');
    const dueDateElement = listItem.querySelector('.task-due-date-time .due-info:nth-of-type(1)');
    const dueTimeElement = listItem.querySelector('.task-due-date-time .due-info:nth-of-type(2)');

    const taskName = taskNameElement ? taskNameElement.textContent.trim() : '';
    const taskDueDateValue = dueDateElement ? dueDateElement.textContent.trim() : '';
    const taskDueTimeValue = dueTimeElement ? dueTimeElement.textContent.trim() : '';
    const priority = listItem.classList.contains('task-priority-high') ? 'high' :
        listItem.classList.contains('task-priority-medium') ? 'medium' : 'low';

    const editDialog = document.createElement('div');
    editDialog.classList.add('edit-dialog');
    editDialog.innerHTML = `
        <div class="edit-dialog-content">
            <h3>Edit Task</h3>
            <input type="text" id="edit-task-name" class="form-control" value="${taskName}">
            <input type="date" id="edit-task-due-date" class="form-control" value="${taskDueDateValue}">
            <input type="time" id="edit-task-due-time" class="form-control" value="${taskDueTimeValue}">
            <select id="edit-task-priority" class="form-control">
                <option value="low" ${priority === 'low' ? 'selected' : ''}>Low Priority</option>
                <option value="medium" ${priority === 'medium' ? 'selected' : ''}>Medium Priority</option>
                <option value="high" ${priority === 'high' ? 'selected' : ''}>High Priority</option>
            </select>
            <button id="save-edit" class="btn btn-primary">Save</button>
            <button id="cancel-edit" class="btn btn-secondary">Cancel</button>
        </div>
    `;

    document.body.appendChild(editDialog);

    document.getElementById('save-edit').onclick = function () {
        const editedTaskName = document.getElementById('edit-task-name').value.trim();
        const editedTaskDueDate = document.getElementById('edit-task-due-date').value;
        const editedTaskDueTime = document.getElementById('edit-task-due-time').value;
        const editedTaskPriority = document.getElementById('edit-task-priority').value;

        // Validate task name
        if (!editedTaskName) {
            alert('Task name cannot be empty.');
            return;
        }

        updateTaskData(taskId, editedTaskName, editedTaskDueDate, editedTaskDueTime, editedTaskPriority);
    };

    document.getElementById('cancel-edit').onclick = function () {
        document.body.removeChild(editDialog);
    };
}

async function updateTaskData(taskId, taskName, dueDate, dueTime, priority) {
    console.log('Updating task with ID:', taskId);

    // Create the task object with updated details
    const updatedTask = {
        taskId: taskId,
        taskName: taskName,
        dueDate: dueDate,
        dueTime: dueTime,
        priority: priority,
        completed: false // Assuming the task completion status is not changed here
    };

    try {
        // Send the PUT request to update the task
        const response = await fetch(`http://localhost:8080/TodoApp/tasks?taskId=${taskId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(updatedTask)
        });

        if (response.ok) {
            const updatedTaskResponse = await response.json();
            console.log('Task updated successfully:', updatedTaskResponse);
            showAllTasks(); // Refresh the task list after updating
        } else {
            console.error('Failed to update task:', response.statusText);
        }
    } catch (error) {
        console.error('Error updating task:', error);
    } finally {
        // Remove the edit dialog
        const editDialog = document.querySelector('.edit-dialog');
        if (editDialog) {
            document.body.removeChild(editDialog);
        }
    }
}


function scheduleNotification(task, dueDate, dueTime) {
    if (!("Notification" in window)) {
        alert("This browser does not support desktop notifications");
        return;
    }

    if (Notification.permission === "granted") {
        createNotification(task, dueDate, dueTime);

    } else if (Notification.permission !== "denied") {
        Notification.requestPermission().then((permission) => {
            if (permission === "granted") {
                createNotification(task, dueDate, dueTime);
            }
        });
    }
}

function createNotification(task, dueDate, dueTime) {
    const dueDateTime = new Date(`${dueDate}T${dueTime}`).getTime(); //this is due date and time in milliseconds
    const currentTime = Date.now();

    const tenMinutesBefore = dueDateTime - 10 * 60 * 1000; // 10 minutes before
    const oneMinuteBefore = dueDateTime - 1 * 60 * 1000; // 1 minute before
    const atDueTime = dueDateTime; // Exact due time

    // Schedule the notification for 10 minutes before
    if (tenMinutesBefore > currentTime) {
        setTimeout(() => {
            console.log(`Scheduling 10-minute reminder for task "${task}"`);
            new Notification("Task Reminder", {
                body: `Task: ${task}\nDue: ${dueDate} ${dueTime}\nDue in 10 minutes`,
            });
        }, tenMinutesBefore - currentTime);
    } else {
        console.log("10-minute reminder skipped (time already passed).");
    }

    // Schedule the notification for 1 minute before
    if (oneMinuteBefore > currentTime) {
        setTimeout(() => {
            console.log(`Scheduling 1-minute reminder for task "${task}"`);
            new Notification("Task Reminder", {
                body: `Task: ${task}\nDue: ${dueDate} ${dueTime}\nDue in 1 minutes`,
            });
        }, oneMinuteBefore - currentTime);
    } else {
        console.log("1-minute reminder skipped (time already passed).");
    }

    // Schedule the notification for the exact due time
    if (atDueTime > currentTime) {
        setTimeout(() => {
            console.log(`Scheduling exact time reminder for task "${task}"`);
            new Notification("Task Reminder", {
                body: `Task: ${task}\nDue: ${dueDate} ${dueTime}`,
            });
        }, atDueTime - currentTime);
    } else {
        console.log("Exact time reminder skipped (time already passed).");
    }
}


function initializeEditDeleteEvents() {
    const taskItems = listContainer.querySelectorAll('.task-item');
    taskItems.forEach(listItem => {
        attachEditDeleteEvents(listItem);
    });
}

function attachEditDeleteEvents(listItem) {
    listItem.querySelector('.edit-task').onclick = function (event) {
        event.preventDefault();
        showEditDialog(listItem);
    };

    listItem.querySelector('.delete-task').onclick = function (event) {
        event.preventDefault();
        deleteTask(listItem);
    };

    listItem.querySelector('.markAsComplete').onclick = function (event) {
        event.preventDefault();
        markAsComplete(listItem);
    };
}
async function deleteTask(listItem) {
    console.log('Deleting task item:', listItem);

    // Retrieve the taskId from the hidden div
    const taskIdElement = listItem.querySelector('div[style="display: none;"]');
    const taskId = taskIdElement ? taskIdElement.textContent.trim() : null;

    if (!taskId) {
        console.error('Task ID not found.');
        return;
    }

    try {
        // Make the API call to delete the task
        const response = await fetch(`http://localhost:8080/TodoApp/tasks?taskId=${taskId}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            console.log(`Task with ID ${taskId} deleted successfully.`);
            showAllTasks(); // Refresh the task list after deletion
        } else {
            console.error(`Failed to delete task with ID ${taskId}:`, response.statusText);
        }
    } catch (error) {
        console.error('Error deleting task:', error);
    }
}

function markAsComplete(listItem) {
    // Retrieve the task ID from the hidden div
    const taskIdElement = listItem.querySelector('div[style="display: none;"]');
    const taskId = taskIdElement ? taskIdElement.textContent.trim() : null;

    if (taskId) {
        // Call the API to mark the task as completed
        fetch('http://localhost:8080/TodoApp/tasks', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({
                taskId: taskId,
                markAsCompleted: true
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to mark task as completed');
            }
            return response.json();
        })
        .then(data => {
            console.log('Task marked as completed:', data);
            // Refresh the UI
            showAllTasks();
        })
        .catch(error => {
            console.error('Error:', error);
        });
    } else {
        console.error('Task ID not found');
    }
}

function initializeSortable() {
    new Sortable(listContainer, {
        animation: 150,
        onEnd: function () {

        }
    });

    new Sortable(document.getElementById('completed-list-container'), {
        animation: 150,
        onEnd: function () {

        }
    });
}

function initializeEllipsisMenu() {
    const ellipsisMenus = document.querySelectorAll('.ellipsis-menu');
    ellipsisMenus.forEach(ellipsis => {
        const listItem = ellipsis.closest('.list-group-item');
        attachEllipsisEvent(ellipsis, listItem);
    });
}

searchBar.addEventListener('input', function () {
    const filter = searchBar.value.toLowerCase();
    const tasks = listContainer.getElementsByClassName('task-item');

    Array.from(tasks).forEach(task => {
        const taskText = task.textContent || task.innerText;
        task.style.display = taskText.toLowerCase().includes(filter) ? '' : 'none';
    });
});

filterTasks.addEventListener('change', function () {
    const filter = filterTasks.value; //low or medium or hard
    const tasks = listContainer.getElementsByClassName('task-item');

    Array.from(tasks).forEach(task => {
        task.style.display = (filter === 'all' || task.classList.contains(`task-priority-${filter}`)) ? '' : 'none';
    });
});


darkModeToggle.addEventListener('click', function () {
    document.body.classList.toggle('dark-mode');
    localStorage.setItem('darkMode', document.body.classList.contains('dark-mode'));
});

// Initialize dark mode based on user preference
if (localStorage.getItem('darkMode') === 'true') {
    document.body.classList.add('dark-mode');
}