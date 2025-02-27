/**
 * 
 */

const getParam = function(attribute){
	const param = {};
	const selector = attribute?`input[id][${attribute}]`:'input[id]';
	const paramObjs = document.querySelectorAll(selector);
	for(const paramObj of paramObjs){
		param[paramObj.id] = paramObj.value;
	}
	return param;
}
const isFunc = function(func){
	return typeof func === 'function';
}
const getData = async function(res, conf){
	if(conf.isRes){
		return res;
	}
	if(res.ok){
		const data = await res.json();
		if(!isFunc(conf.callback)){
			return data;
		}
		return conf.callback(data);
	}
	const error = await res.text();
	if(!isFunc(conf.errorCallback)){
		conf.errorCallback(error);	
	}
	throw new Error(error);
}
const getFetch = async function(conf){
	const url = conf || conf.url;
	const res = await fetch(url);
	return await getData(res, conf);
}
const comFetch = async function(conf){
	conf.method = conf.method?conf.method:'GET';
	const res = await fetch(conf.url, conf);
	return await getData(res, conf);
}