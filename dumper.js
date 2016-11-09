let project = 199352541227; // Setup here your project id (https://app.asana.com/0/199352541227/list, 199352541227 is project id)
window.result = {}
fetch(`https://app.asana.com/api/1.0/projects/${project}/tasks`, {credentials: 'same-origin'})
	.then(res => res.json()).then(data => {
		let tasks = [];
		data.data.forEach(el => {
			tasks.push(el)
			result[el.id] = el;
			result[el.id].stories = [];
			});
		console.log(`Taked ${tasks.length} tasks`);
		return tasks;
		})
	.then(tasks => {
		let promises = []
		let counter = 0;
		tasks.forEach(task => {
			promises.push(fetch(`https://app.asana.com/api/1.0/tasks/${task.id}/stories`, {credentials: 'same-origin'})
				.then(res => {
					return res.json()
					})
				.then(data => {
					console.log(`Handled ${counter++} task`);
					data.data.forEach(i => {
						window.result[task.id].stories.push(i);
						});
					return true;
					}));
			});
			return Promise.all(promises);
		}).then((values) => {
			console.log('Finish');
			window.document.close();
			window.document.write(JSON.stringify(result));
			})
